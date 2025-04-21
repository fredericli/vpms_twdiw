package example.vpms.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import example.vpms.model.Visitor;
import example.vpms.service.VisitorService;
import example.vpms.twdiw.verify.config.TWDIWVerifyConfig;
import example.vpms.twdiw.verify.dto.QrcodeResponseDto;
import example.vpms.twdiw.verify.dto.VerifyResultDto;
import example.vpms.twdiw.verify.service.TWDIWVerifyService;

/**
 * 數位憑證皮夾QR碼API控制器
 */
@RestController
@RequestMapping("/api/oidvp")
public class VPController {
    
    private static final Logger logger = LoggerFactory.getLogger(VPController.class);
    
    @Autowired
    private TWDIWVerifyService verifyService;
    
    @Autowired
    private VisitorService visitorService;
    
    @Autowired
    private TWDIWVerifyConfig verifyConfig;
    
    /**
     * 產生授權請求QR碼
     * 
     * @param ref 證件類型
     * @param transactionId 交易ID
     * @return QR碼回應
     */
    @GetMapping("/qr-code")
    public ResponseEntity<?> generateQRCode(
            @RequestParam(value = "ref", required = false) String ref,
            @RequestParam(value = "transaction_id", required = false) String transactionId) {
        
        // 若未提供 ref 參數，則使用配置中的默認值
        String certRef = (ref != null && !ref.isEmpty()) ? ref : verifyConfig.getDefaultRef();
        
        logger.info("接收到QR碼生成請求，證件類型: {}, 交易ID: {}", certRef, transactionId);
        
        try {
            return generateAndValidateQrCode(certRef, transactionId);
        } catch (RestClientException e) {
            return handleApiCommunicationError(e);
        } catch (Exception e) {
            return handleGeneralQrCodeError(e);
        }
    }
    
    /**
     * 產生並驗證 QR 碼
     */
    private ResponseEntity<?> generateAndValidateQrCode(String certRef, String transactionId) {
        QrcodeResponseDto response;
        
        // 產生 QR 碼
        if (transactionId != null && !transactionId.isEmpty()) {
            response = verifyService.generateQrcode(certRef, transactionId);
        } else {
            response = verifyService.generateQrcode(certRef);
        }
        
        // 檢查回應內容是否有效 - 同時支援新舊格式
        if (isValidQrCodeResponse(response)) {
            logger.info("QR碼生成成功，交易ID: {}", 
                       response.getTransactionId() != null ? response.getTransactionId() : transactionId);
            return ResponseEntity.ok(response);
        } else {
            logger.error("QR碼生成失敗，回應內容無效，交易ID: {}", transactionId);
            return createErrorResponse("生成QR碼失敗，請稍後再試", HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 檢查QR碼回應是否有效
     */
    private boolean isValidQrCodeResponse(QrcodeResponseDto response) {
        return response != null && 
            ((response.getQrcodeImage() != null && response.getAuthUri() != null) || 
             (response.getData() != null && response.getData().getQrcode() != null));
    }
    
    /**
     * 處理 API 通訊錯誤
     */
    private ResponseEntity<?> handleApiCommunicationError(RestClientException e) {
        // logger.error("與數位憑證皮夾API通訊失敗", e);
        return createErrorResponse("與數位憑證皮夾API通訊失敗: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    /**
     * 處理一般QR碼錯誤
     */
    private ResponseEntity<?> handleGeneralQrCodeError(Exception e) {
        logger.error("生成QR碼時發生未知錯誤", e);
        return createErrorResponse("生成QR碼時發生未知錯誤: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * 查詢QR碼掃描結果
     * 
     * @param transactionId 交易ID
     * @return 掃描結果數據
     */
    @GetMapping("/result")
    public ResponseEntity<?> getQRCodeResult(
            @RequestParam(value = "transaction_id") String transactionId) {
        
        // logger.info("接收到QR碼掃描結果查詢請求，交易ID: {}", transactionId);
        
        try {
            // 透過驗證服務查詢結果
            VerifyResultDto result = verifyService.getVerifyResult(transactionId);
            
            if (result != null) {
                return processSuccessfulResult(result, transactionId);
            } else {
                logger.warn("查詢掃描結果返回空值，交易ID: {}", transactionId);
                return ResponseEntity.notFound().build();
            }
        } catch (RestClientException e) {
            return handleResultApiError(e, transactionId);
        } catch (Exception e) {
            return handleGeneralResultError(e);
        }
    }
    
    /**
     * 處理成功獲取的掃描結果
     */
    private ResponseEntity<?> processSuccessfulResult(VerifyResultDto result, String transactionId) {
        logger.info("成功獲取掃描結果，交易ID: {}, 驗證結果: {}", 
                   transactionId, result.getVerifyResult());
        
        // 儲存訪客資訊
        try {
            saveVisitorInfoFromResult(result);
        } catch (Exception ex) {
            logger.error("寫入訪客記錄時發生錯誤", ex);
            // 不影響主流程，繼續返回掃描結果
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 從驗證結果中儲存訪客資訊
     */
    private void saveVisitorInfoFromResult(VerifyResultDto result) {
        if (result.getData() != null && !result.getData().isEmpty()) {
            // 使用標準變數存儲訪客資訊
            final Holder<String> nameHolder = new Holder<>(null);
            final Holder<String> companyHolder = new Holder<>("未知公司");
            final Holder<String> emailHolder = new Holder<>("未知");
            
            logger.info("收到的result.getData()是:{}", result.getData());
            
            // 從宣告中獲取相關資訊
            for (VerifyResultDto.CredentialData credentialData : result.getData()) {
                if (credentialData.getClaims() != null) {
                    for (VerifyResultDto.Claim claim : credentialData.getClaims()) {
                        String claimName = claim.getEname();
                        String claimValue = claim.getValue();
                        
                        if ("name".equals(claimName)) {
                            nameHolder.value = claimValue;
                        } else if ("company".equals(claimName)) {
                            companyHolder.value = claimValue;
                        } else if ("email".equals(claimName)) {
                            emailHolder.value = claimValue;
                        }
                    }
                }
            }
            
            // 如果至少有姓名資訊，則創建訪客記錄
            if (nameHolder.value != null && !nameHolder.value.isEmpty()) {
                Visitor visitor = new Visitor(
                    companyHolder.value, 
                    null,  // department 設為 null
                    nameHolder.value, 
                    emailHolder.value
                );
                visitorService.registerVisitor(visitor);
                logger.info("已將掃描資訊寫入訪客記錄，訪客姓名: {}", nameHolder.value);
            } else {
                logger.warn("掃描結果中未包含姓名資訊，無法創建訪客記錄");
            }
        }
    }
    
    /**
     * 持有值的簡單容器類，用於 lambda 表達式中的非 final 變數
     */
    private static class Holder<T> {
        public T value;
        
        public Holder(T initialValue) {
            this.value = initialValue;
        }
    }
    
    /**
     * 處理結果 API 錯誤
     */
    private ResponseEntity<?> handleResultApiError(RestClientException e, String transactionId) {
        // 詳細記錄錯誤
        // logger.error("與數位憑證皮夾API通訊失敗: {}", e.getMessage());
        
        // 處理 HTTP 狀態碼錯誤
        if (e instanceof HttpStatusCodeException) {
            HttpStatusCodeException httpEx = (HttpStatusCodeException) e;
            String responseBody = httpEx.getResponseBodyAsString();
            int statusCode = httpEx.getStatusCode().value();
            
            // logger.error("HTTP錯誤狀態碼: {}, 回應內容: {}", statusCode, responseBody);
            
            // 檢查回應是否為HTML內容（登入頁面）
            if (isHtmlResponseBody(responseBody)) {
                logger.error("收到HTML回應，可能是會話已過期，需要重新登入");
                return createErrorResponse("系統驗證已過期，請重新整理頁面", HttpStatus.UNAUTHORIZED);
            }
            
            // 特別處理503錯誤
            if (statusCode == 503) {
                return createErrorResponse("外部服務暫時不可用，請稍後再試", HttpStatus.SERVICE_UNAVAILABLE);
            }
            
            // 處理404錯誤（QR碼尚未被掃描）
            if (statusCode == 404) {
                logger.info("QR碼尚未被掃描，交易ID: {}", transactionId);
                return ResponseEntity.notFound().build();
            }
            
            // 處理400錯誤，檢查是否為特定的「結果未找到」錯誤
            if (statusCode == 400 && responseBody != null) {
                if (responseBody.contains("verify result not found") || 
                    responseBody.contains("\"code\":4002")) {
                    logger.info("QR碼尚未被掃描 (錯誤代碼4002)，交易ID: {}", transactionId);
                    // 這種情況也視為「尚未掃描」，返回 404
                    return ResponseEntity.notFound().build();
                }
            }
        } else if (e.getMessage() != null && e.getMessage().contains("404")) {
            // 處理不是 HttpStatusCodeException 但包含 404 的錯誤訊息
            logger.info("QR碼尚未被掃描（來自訊息內容），交易ID: {}", transactionId);
            return ResponseEntity.notFound().build();
        }
        
        // 其他 RestClient 錯誤
        return createErrorResponse("查詢掃描結果失敗: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    /**
     * 檢查回應內容是否為 HTML
     */
    private boolean isHtmlResponseBody(String responseBody) {
        return responseBody != null && 
                (responseBody.trim().startsWith("<!DOCTYPE html>") || 
                 responseBody.contains("<html>") || 
                 responseBody.contains("<title>管理員登入"));
    }
    
    /**
     * 處理一般結果錯誤
     */
    private ResponseEntity<?> handleGeneralResultError(Exception e) {
        logger.error("查詢掃描結果時發生未預期錯誤", e);
        return createErrorResponse("查詢掃描結果失敗: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * 獲取系統配置的默認證件類型
     * 
     * @return 默認證件類型
     */
    @GetMapping("/default-ref")
    public ResponseEntity<?> getDefaultRef() {
        try {
            String defaultRef = verifyConfig.getDefaultRef();
            logger.info("返回默認證件類型: {}", defaultRef);
            return ResponseEntity.ok(Map.of("defaultRef", defaultRef));
        } catch (Exception e) {
            logger.error("獲取默認證件類型時發生錯誤", e);
            return createErrorResponse("獲取默認證件類型失敗: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 創建錯誤回應
     */
    private ResponseEntity<?> createErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
} 
