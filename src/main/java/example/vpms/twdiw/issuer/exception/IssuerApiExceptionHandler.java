package example.vpms.twdiw.issuer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import example.vpms.twdiw.issuer.dto.ApiErrorResponse;
import example.vpms.twdiw.issuer.enums.IssuerHttpStatus;

/**
 * 發行端 API 例外處理器
 */
@RestControllerAdvice
public class IssuerApiExceptionHandler {
    
    /**
     * 處理發行端 API 例外
     * 
     * @param ex 發行端 API 例外
     * @return 錯誤回應
     */
    @ExceptionHandler(IssuerApiException.class)
    public ResponseEntity<ApiErrorResponse> handleIssuerApiException(IssuerApiException ex) {
        ApiErrorResponse error = new ApiErrorResponse(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 處理 HTTP 客戶端錯誤
     * 
     * @param ex HTTP 客戶端錯誤例外
     * @return 錯誤回應
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpClientErrorException(HttpClientErrorException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                String.valueOf(ex.getStatusCode().value()),
                "客戶端錯誤: " + ex.getMessage()
        );
        return new ResponseEntity<>(error, ex.getStatusCode());
    }
    
    /**
     * 處理 HTTP 服務端錯誤
     * 
     * @param ex HTTP 服務端錯誤例外
     * @return 錯誤回應
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpServerErrorException(HttpServerErrorException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                String.valueOf(ex.getStatusCode().value()),
                "服務端錯誤: " + ex.getMessage()
        );
        return new ResponseEntity<>(error, ex.getStatusCode());
    }
    
    /**
     * 處理資源訪問錯誤
     * 
     * @param ex 資源訪問例外
     * @return 錯誤回應
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceAccessException(ResourceAccessException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                IssuerHttpStatus.INTERNAL_SERVER_ERROR.getCode(),
                "無法連接到發行端 API: " + ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * 處理一般例外
     * 
     * @param ex 一般例外
     * @return 錯誤回應
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                IssuerHttpStatus.INTERNAL_SERVER_ERROR.getCode(),
                "系統錯誤: " + ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 