package example.vpms.twdiw.issuer.exception;

import example.vpms.twdiw.issuer.enums.IssuerHttpStatus;

/**
 * 發行端 API 例外
 */
public class IssuerApiException extends RuntimeException {
    
    private final String code;
    
    /**
     * 使用 HTTP 狀態碼枚舉建立例外
     * 
     * @param httpStatus HTTP 狀態碼枚舉
     */
    public IssuerApiException(IssuerHttpStatus httpStatus) {
        super(httpStatus.getMessage());
        this.code = httpStatus.getCode();
    }
    
    /**
     * 自定義錯誤碼和訊息建立例外
     * 
     * @param code 錯誤碼
     * @param message 錯誤訊息
     */
    public IssuerApiException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    /**
     * 獲取錯誤碼
     * 
     * @return 錯誤碼
     */
    public String getCode() {
        return code;
    }
} 