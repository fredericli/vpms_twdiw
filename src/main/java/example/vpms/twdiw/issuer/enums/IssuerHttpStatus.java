package example.vpms.twdiw.issuer.enums;

/**
 * 發行端 HTTP 狀態碼枚舉
 */
public enum IssuerHttpStatus {
    
    /**
     * 請求成功
     */
    OK("200", "請求成功"),
    
    /**
     * 請求已建立
     */
    CREATED("201", "請求已建立"),
    
    /**
     * 客戶端錯誤
     */
    BAD_REQUEST("400", "client端錯誤"),
    
    /**
     * 服務端錯誤
     */
    INTERNAL_SERVER_ERROR("500", "server端錯誤");
    
    private final String code;
    private final String message;
    
    IssuerHttpStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
} 