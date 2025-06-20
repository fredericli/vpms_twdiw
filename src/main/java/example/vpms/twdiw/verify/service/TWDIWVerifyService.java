package example.vpms.twdiw.verify.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import example.vpms.twdiw.verify.config.TWDIWVerifyConfig;
import example.vpms.twdiw.verify.dto.QrcodeResponseDto;
import example.vpms.twdiw.verify.dto.VerifyResultDto;

/**
 * 數位憑證皮夾驗證端服務
 */
@Service
public class TWDIWVerifyService {
    
    private static final Logger logger = LoggerFactory.getLogger(TWDIWVerifyService.class);
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private TWDIWVerifyConfig config;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 產生授權請求 QR 碼
     * 
     * @param ref 證件類型
     * @return QR碼回應
     */
    public QrcodeResponseDto generateQrcode(String ref) {
        String transactionId = UUID.randomUUID().toString();
        return generateQrcode(ref, transactionId);
    }
    
    /**
     * 產生授權請求 QR 碼，使用指定的交易 ID
     * 
     * @param ref 證件類型
     * @param transactionId 交易 ID
     * @return QR碼回應
     * @throws RestClientException 如果API請求失敗
     */
    public QrcodeResponseDto generateQrcode(String ref, String transactionId) {
        logger.info("產生授權請求QR碼，證件類型: {}, 交易ID: {}", ref, transactionId);
        
        try {
            HttpHeaders headers = createHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            String url = config.getQrcodeUrl();
            logger.debug("API呼叫URL: {}", url);
            
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("ref", ref)
                    .queryParam("transactionId", transactionId);
            
            String fullUrl = uriBuilder.toUriString();
            logger.debug("完整請求URL: {}", fullUrl);
            
            // 先嘗試以字串形式獲取回應
            ResponseEntity<String> stringResponse = restTemplate.exchange(
                    fullUrl,
                    HttpMethod.GET,
                    entity,
                    String.class);
            
            logger.info("HTTP CODE：{}", stringResponse.getStatusCode());
                    // 檢查回應內容類型
            String contentType = stringResponse.getHeaders().getFirst("Content-Type");
            if (contentType != null && contentType.contains("text/html")) {
                logger.error("API返回HTML錯誤頁面: {}", stringResponse.getBody());
                throw new RestClientException("API服務暫時不可用，請稍後再試");
            }
            
            // 如果內容類型是JSON，則解析為QrcodeResponseDto
            try {
                QrcodeResponseDto responseDto = objectMapper.readValue(stringResponse.getBody(), QrcodeResponseDto.class);
                logger.info("QR碼產生HTTP結果: {}", stringResponse.getStatusCode());
                
                if (responseDto != null) {
                    logger.debug("收到回應: code={}, message={}", responseDto.getCode(), responseDto.getMessage());
                } else {
                    logger.warn("收到空的回應內容");
                }
                
                return responseDto;
            } catch (Exception e) {
                logger.error("解析API回應失敗: {}", e.getMessage());
                throw new RestClientException("解析API回應失敗: " + e.getMessage());
            }
        } catch (RestClientException e) {
            logger.error("呼叫數位憑證皮夾API失敗: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("產生QR碼時發生未預期錯誤: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 查詢VP展示驗證結果
     * 
     * @param transactionId 交易 ID
     * @return 驗證結果
     */
    public VerifyResultDto getVerifyResult(String transactionId) {
        return getVerifyResult(transactionId, null);
    }
    
    /**
     * 查詢VP展示驗證結果，使用指定的回應碼
     * 
     * @param transactionId 交易 ID
     * @param responseCode 回應碼
     * @return 驗證結果
     */
    public VerifyResultDto getVerifyResult(String transactionId, String responseCode) {
        // logger.debug("查詢VP展示驗證結果，交易ID: {}, 回應碼: {}", transactionId, responseCode);
        
        HttpHeaders headers = createHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(config.getVerifyResultUrl())
                .queryParam("transactionId", transactionId);
        
        if (responseCode != null && !responseCode.isEmpty()) {
            uriBuilder.queryParam("response_code", responseCode);
        }
        
        ResponseEntity<VerifyResultDto> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                entity,
                VerifyResultDto.class);
        
        logger.info("驗證結果查詢狀態: {}", response.getStatusCode());
        VerifyResultDto result = response.getBody();
        
        try {
            // 將物件轉換為 JSON 字串，以便在日誌中顯示每個欄位的鍵值對
            String resultJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
            logger.info("驗證結果詳細內容:\n{}", resultJson);
        } catch (Exception e) {
            logger.error("轉換驗證結果為 JSON 時發生錯誤: {}", e.getMessage());
            logger.info("驗證結果內容物: {}", result);
        }
        
        return result;
    }
    
    /**
     * 創建包含 API Key 的請求頭
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        logger.info("API Key: {}", config.getAccessToken());
        headers.set("Access-Token", config.getAccessToken());
        return headers;
    }
} 
