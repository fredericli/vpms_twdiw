package example.vpms.twdiw.issuer.dto;

/**
 * 發行端 VC 卡片單一資料查詢回應 DTO
 */
public class DwIssuerVC402iResDTO {
    
    private String code;
    
    private String message;
    
    private String credential;

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

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }
} 