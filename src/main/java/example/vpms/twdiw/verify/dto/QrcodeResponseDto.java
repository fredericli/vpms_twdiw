package example.vpms.twdiw.verify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 驗證端產生授權請求 QRcode 的响應 DTO
 */
public class QrcodeResponseDto {
    
    @JsonProperty("transactionId")
    private String transactionId;
    
    @JsonProperty("qrcodeImage")
    private String qrcodeImage;
    
    @JsonProperty("authUri")
    private String authUri;
    
    // 為相容性保留的舊欄位
    private String code;
    private String message;
    private QrcodeData data;
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getQrcodeImage() {
        return qrcodeImage;
    }
    
    public void setQrcodeImage(String qrcodeImage) {
        this.qrcodeImage = qrcodeImage;
    }
    
    public String getAuthUri() {
        return authUri;
    }
    
    public void setAuthUri(String authUri) {
        this.authUri = authUri;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public QrcodeData getData() {
        // 為相容性創建資料對象
        if (data == null && qrcodeImage != null) {
            QrcodeData newData = new QrcodeData();
            newData.setQrcode(qrcodeImage.replace("data:image/png;base64,", ""));
            newData.setAuthUri(authUri);
            return newData;
        }
        return data;
    }
    
    public void setData(QrcodeData data) {
        this.data = data;
    }
    
    public static class QrcodeData {
        @JsonProperty("auth_uri")
        private String authUri;
        private String qrcode;
        
        public String getAuthUri() {
            return authUri;
        }
        
        public void setAuthUri(String authUri) {
            this.authUri = authUri;
        }
        
        public String getQrcode() {
            return qrcode;
        }
        
        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }
    }
} 