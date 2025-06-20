package example.vpms.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import example.vpms.dto.VisitorCardDto;
import example.vpms.model.VCApply;
import example.vpms.repository.VCApplyRepository;
import example.vpms.service.VisitorCardService;
import example.vpms.twdiw.issuer.dto.DwIssuerVC402iResDTO;
import example.vpms.twdiw.issuer.dto.VCItemDataDTO;
import example.vpms.twdiw.issuer.service.IssuerApiService;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;

/**
 * 訪客領卡 API 控制器
 */
@RestController
@RequestMapping("/api/visitor")
public class VCController {

    private static final Logger logger = LoggerFactory.getLogger(VCController.class);

    private final VisitorCardService visitorCardService;
    private final IssuerApiService issuerApiService;
    private final VCApplyRepository vcApplyRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public VCController(VisitorCardService visitorCardService, IssuerApiService issuerApiService,
            VCApplyRepository vcApplyRepository, JavaMailSender mailSender) {
        this.visitorCardService = visitorCardService;
        this.issuerApiService = issuerApiService;
        this.vcApplyRepository = vcApplyRepository;
        this.mailSender = mailSender;
    }

    /**
     * 處理訪客領卡請求
     * 
     * @param cardDto 訪客領卡資料
     * @param result  驗證結果
     * @return 回應實體
     */
    @PostMapping("/card")
    public ResponseEntity<?> handleVisitorCard(@Valid @RequestBody VisitorCardDto cardDto, BindingResult result) {
        // 檢查是否有驗證錯誤
        if (result.hasErrors()) {
            return createValidationErrorResponse(result);
        }
        
        try {
            logger.info("接收到訪客領卡請求: {}", cardDto);
            
            return processVisitorCardRequest(cardDto);
        } catch (Exception e) {
            logger.error("訪客領卡處理錯誤", e);
            return createErrorResponse("處理訪客領卡請求時發生錯誤: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 創建驗證錯誤的回應
     */
    private ResponseEntity<?> createValidationErrorResponse(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * 處理訪客領卡請求並返回回應
     */
    private ResponseEntity<?> processVisitorCardRequest(VisitorCardDto cardDto) {
        // 處理訪客領卡請求並發行 VC
        VCItemDataDTO vcItemData = visitorCardService.processVisitorCard(cardDto);

        // 創建回應物件
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "訪客領卡資料提交成功");

        // 添加 VC 卡片資訊
        if (vcItemData != null) {
            response.put("cardInfo", createCardInfoMap(vcItemData));
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 從 VCItemDataDTO 建立卡片資訊 Map
     */
    private Map<String, Object> createCardInfoMap(VCItemDataDTO vcItemData) {
        Map<String, Object> cardInfo = new HashMap<>();
        cardInfo.put("id", vcItemData.getId());
        cardInfo.put("qrCode", vcItemData.getQrCode());

        // 處理 deepLink 中的換行符
        String deepLink = vcItemData.getDeepLink();
        if (deepLink != null) {
            deepLink = deepLink.replace("\r\n", "").replace("\n", "");
        }
        cardInfo.put("deepLink", deepLink);

        // 添加交易ID，用於後續查詢掃描狀態
        cardInfo.put("transactionId", vcItemData.getTransactionId());

        return cardInfo;
    }

    /**
     * 檢查訪客卡是否已被掃描
     * 
     * @param transactionId 交易ID（nonce）
     * @return 掃描狀態
     */
    @GetMapping("/card/scan-status/{transactionId}")
    public ResponseEntity<?> checkScanStatus(@PathVariable String transactionId) {
        logger.info("檢查訪客卡掃描狀態，交易ID: {}", transactionId);

        Map<String, Object> response = new HashMap<>();

        try {
            // 調用 IssuerApiService 獲取掃描狀態
            DwIssuerVC402iResDTO result = issuerApiService.getVCItemDataByNonce(transactionId);
            return handleSuccessfulScanResult(result, transactionId, response);
        } catch (HttpClientErrorException e) {
            return handleHttpClientError(e, transactionId, response);
        } catch (HttpServerErrorException e) {
            logger.info("訪客卡尚未被掃描，交易ID: {}", transactionId);
            response.put("status", "pending");
            response.put("message", "訪客卡尚未被掃描");
            return ResponseEntity.ok(response);
            // return handleHttpServerError(e, response);
        } catch (Exception e) {
            return handleGenericError(e, response);
        }
    }

    /**
     * 檢查訪客卡處理狀態
     * 
     * @param requestId 請求ID
     * @return 處理狀態
     */
    @GetMapping("/card/status")
    public ResponseEntity<?> checkCardStatus(String requestId) {
        logger.info("檢查訪客卡處理狀態，請求ID: {}", requestId);

        if (requestId == null || requestId.trim().isEmpty()) {
            return createErrorResponse("請求ID不能為空", HttpStatus.BAD_REQUEST);
        }

        // 這裡是模擬狀態檢查邏輯，實際應用中需替換成真實的狀態檢查
        // 在實際情況中，可能需要查詢資料庫或外部服務來確定狀態

        Map<String, Object> response = new HashMap<>();

        // 根據不同時間點模擬不同的處理狀態
        long timestamp = System.currentTimeMillis();
        if (timestamp % 10 < 3) {
            // 30% 的情況返回處理中
            response.put("status", "PROCESSING");
            response.put("message", "訪客卡正在處理中，請稍候...");
        } else if (timestamp % 10 < 9) {
            // 60% 的情況返回成功
            response.put("status", "SUCCESS");
            response.put("message", "訪客卡處理成功");
        } else {
            // 10% 的情況返回失敗
            response.put("status", "FAILED");
            response.put("message", "訪客卡處理失敗，請重試");
            response.put("errorCode", "CARD_PROCESSING_ERROR");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 處理成功獲取掃描結果的情況
     */
    private ResponseEntity<?> handleSuccessfulScanResult(DwIssuerVC402iResDTO result, String transactionId,
            Map<String, Object> response) {
        logScanResultDetails(result);

        // 檢查是否已存在相同的交易識別碼
        Optional<VCApply> existingApply = vcApplyRepository.findByTransactionId(transactionId);
        if (existingApply.isPresent()) {

            // 檢查 email 是否已存在且狀態為 OK
            List<VCApply> existingApplies = vcApplyRepository.findAllByEmailAndStatus(existingApply.get().getEmail(), "OK");
            if (!existingApplies.isEmpty()) {
                for(VCApply apply : existingApplies) {
                    // 去觸發廢止機制
                    if(StringUtils.isNotBlank(apply.getCid())) {
                        logger.info("觸發廢止機制，廢止 CID: {}", apply.getCid());
                        issuerApiService.vCItemDataRevoke(apply.getCid(), "revocation");
                        this.sendCardRevocationEmail(apply);
                        // 更新狀態為 REVOKED
                        apply.setStatus("REVOKED");
                        vcApplyRepository.save(apply);
                    }
                }
            }

            // 更新現有記錄的 credential
            VCApply vcApply = existingApply.get();
            vcApply.setCredential(result.getCredential());

            // 解析 JWT Token 並處理 jti 值
            try {
                String credential = result.getCredential();
                if (credential != null && !credential.isEmpty()) {
                    // 解析 JWT Token
                    String[] parts = credential.split("\\.");
                    if (parts.length == 3) {
                        // 解碼 payload (第二部分)
                        String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
                        // 解析 JSON
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode jsonNode = mapper.readTree(payload);

                        // 取得 jti 值
                        String jti = jsonNode.get("jti").asText();
                        if (jti != null && jti.startsWith("https://issuer-vc.wallet.gov.tw/api/credential/")) {
                            // 移除前綴並設定 cid
                            String cid = jti.replace("https://issuer-vc.wallet.gov.tw/api/credential/", "");
                            vcApply.setCid(cid);
                            logger.info("已設定 CID: {}", cid);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("解析 JWT Token 時發生錯誤", e);
            }

            vcApply.setUpdatedAt(LocalDateTime.now());
            vcApply.setStatus("OK");
            vcApplyRepository.save(vcApply);
            logger.info("已更新 VC_APPLY 記錄的 credential，交易識別碼：{}", transactionId);
        } else {
            logger.warn("找不到對應的 VC_APPLY 記錄，交易識別碼：{}", transactionId);
        }

        // 若成功獲取結果，表示已經被掃描
        response.put("status", "scanned");
        response.put("message", "掃描成功");

        // 建立卡片資訊
        Map<String, Object> cardInfo = new HashMap<>();
        cardInfo.put("credential", result.getCredential());
        response.put("cardInfo", cardInfo);

        return ResponseEntity.ok(response);
    }

    /**
     * 記錄掃描結果詳細信息
     */
    private void logScanResultDetails(DwIssuerVC402iResDTO result) {
        if (result != null) {
            logger.info("收到的 DwIssuerVC402iResDTO 資訊:");
            logger.info("- code: {}", result.getCode());
            logger.info("- message: {}", result.getMessage());

            // 安全記錄 credential 內容
            String credential = result.getCredential();
            if (credential != null && !credential.isEmpty()) {
                // 記錄長度而不是全部內容
                logger.info("- credential 長度: {} 字符", credential.length());

                // 嘗試判斷 credential 格式，但不記錄內容
                if (credential.trim().startsWith("{") && credential.trim().endsWith("}")) {
                    logger.info("- credential 是 JSON 格式");
                } else if (credential.contains(".") && credential.split("\\.").length >= 2) {
                    logger.info("- credential 可能是 JWT 格式");
                    String[] parts = credential.split("\\.");
                    logger.info("- JWT 部分數量: {}", parts.length);
                } else {
                    logger.info("- credential 是其他格式");
                }
            } else {
                logger.info("- credential: null 或空白");
            }
        } else {
            logger.warn("收到的 DwIssuerVC402iResDTO 為 null");
        }
    }

    /**
     * 處理 HTTP 客戶端錯誤
     */
    private ResponseEntity<?> handleHttpClientError(HttpClientErrorException e, String transactionId,
            Map<String, Object> response) {
        // 檢查狀態碼
        if (isNotFoundError(e)) {
            logger.info("訪客卡尚未被掃描，交易ID: {}", transactionId);
            response.put("status", "pending");
            response.put("message", "訪客卡尚未被掃描");
            return ResponseEntity.ok(response);
        }

        // 檢查是否收到 HTML 登入頁面
        if (isHtmlLoginPage(e)) {
            logger.error("收到 HTML 登入頁面，會話可能已過期");
            return createErrorResponse("系統驗證已過期，請重新整理頁面", HttpStatus.UNAUTHORIZED);
        }

        // 檢查是否是授權問題
        if (isAuthorizationError(e)) {
            return createErrorResponse("訪客卡檢查驗證失敗，請聯繫管理員", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 檢查是否是服務不可用
        if (isServiceUnavailableError(e)) {
            return createErrorResponse("外部服務暫時不可用，請稍後再試", HttpStatus.SERVICE_UNAVAILABLE);
        }

        // 其他客戶端錯誤
        logger.error("HTTP 客戶端錯誤: {}", e.getMessage());
        return createErrorResponse("檢查掃描狀態時發生錯誤: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 檢查是否為 404 錯誤
     */
    private boolean isNotFoundError(HttpClientErrorException e) {
        return e.getStatusCode().equals(HttpStatus.NOT_FOUND) ||
                (e.getMessage() != null && e.getMessage().contains("404"));
    }

    /**
     * 檢查是否收到 HTML 登入頁面
     */
    private boolean isHtmlLoginPage(HttpClientErrorException e) {
        String responseBody = e.getResponseBodyAsString();
        // 更安全的方式檢查 HTML 內容
        return responseBody != null &&
                (responseBody.toLowerCase().contains("<title>") && responseBody.toLowerCase().contains("登入")) ||
                (responseBody.toLowerCase().contains("<!doctype html>")
                        && responseBody.toLowerCase().contains("login"));
    }

    /**
     * 檢查是否為授權錯誤
     */
    private boolean isAuthorizationError(HttpClientErrorException e) {
        return e.getStatusCode().equals(HttpStatus.UNAUTHORIZED) ||
                e.getStatusCode().equals(HttpStatus.FORBIDDEN);
    }

    /**
     * 檢查是否為服務不可用錯誤
     */
    private boolean isServiceUnavailableError(HttpClientErrorException e) {
        return e.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * 處理一般錯誤
     */
    private ResponseEntity<?> handleGenericError(Exception e, Map<String, Object> response) {
        logger.error("檢查訪客卡掃描狀態時發生錯誤", e);
        response.put("status", "error");
        response.put("message", "檢查掃描狀態時發生錯誤: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 創建錯誤回應
     */
    private ResponseEntity<?> createErrorResponse(String message, HttpStatus status) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", message);
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * 發送訪客卡停用通知郵件
     * 
     * @param vcApply 訪客申請資料
     */
    private void sendCardRevocationEmail(VCApply vcApply) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("noreply@wallet.gov.tw","數位憑證皮夾系統信箱");
            helper.setTo(vcApply.getEmail());
            helper.setSubject("數位發展部訪客卡停用通知");
            
            // HTML 郵件內容
            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333;
                        }
                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                        }
                        .header {
                            background-color: #f8f9fa;
                            padding: 20px;
                            text-align: center;
                            border-radius: 5px;
                        }
                        .content {
                            padding: 20px;
                            background-color: #ffffff;
                            border-radius: 5px;
                            margin-top: 20px;
                        }
                        .footer {
                            text-align: center;
                            margin-top: 20px;
                            color: #666;
                            font-size: 0.9em;
                        }
                        .button {
                            display: inline-block;
                            padding: 10px 20px;
                            background-color: #007bff;
                            color: white;
                            text-decoration: none;
                            border-radius: 5px;
                            margin-top: 20px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>訪客卡停用通知</h2>
                        </div>
                        <div class="content">
                            <p>親愛的 %s：</p>
                            <p>您的數位發展部訪客卡已被停用。</p>
                            <p>若您下次需要進入數位發展部，請重新申請訪客卡。</p>
                            <p>您可以使用以下資訊重新申請：</p>
                            <ul>
                                <li>姓名：%s</li>
                                <li>電子郵件：%s</li>
                                <li>手機號碼：%s</li>
                            </ul>
                            <p>如有任何問題，請聯繫數位發展部管理員。</p>
                        </div>
                        <div class="footer">
                            <p>此郵件由系統自動發送，請勿直接回覆。</p>
                            <p>數位發展部訪客管理系統</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                    vcApply.getName(),
                    vcApply.getName(),
                    vcApply.getEmail(),
                    vcApply.getCellphone()
                );
            
            helper.setText(htmlContent, true);
            mailSender.send(message);
            
            logger.info("已發送訪客卡停用通知郵件至：{}", vcApply.getEmail());
        } catch (Exception e) {
            logger.error("發送訪客卡停用通知郵件失敗：{}", e.getMessage());
        }
    }
}
