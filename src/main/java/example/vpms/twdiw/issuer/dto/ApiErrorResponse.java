package example.vpms.twdiw.issuer.dto;

/**
 * API 錯誤回應 DTO
 */
public class ApiErrorResponse {
    
    private String code;
    
    private String message;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
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
} 