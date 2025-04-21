package example.vpms.twdiw.verify.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 數位憑證皮夾驗證端服務配置
 */
@Configuration
public class TWDIWVerifyConfig {
    
    @Value("${twdiw.verify.api.base-url}")
    private String baseUrl;
    
    @Value("${twdiw.verify.api.access-token}")
    private String accessToken;
    
    @Value("${twdiw.verify.card.default-ref:00000000_demo_01}")
    private String defaultRef;
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public String getDefaultRef() {
        return defaultRef;
    }
    
    public String getQrcodeUrl() {
        return baseUrl + "/oidvp/qr-code";
    }
    
    public String getVerifyResultUrl() {
        return baseUrl + "/oidvp/result";
    }
} 