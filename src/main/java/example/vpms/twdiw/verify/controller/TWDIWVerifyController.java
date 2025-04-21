package example.vpms.twdiw.verify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import example.vpms.twdiw.verify.dto.QrcodeResponseDto;
import example.vpms.twdiw.verify.dto.VerifyResultDto;
import example.vpms.twdiw.verify.service.TWDIWVerifyService;

/**
 * 數位憑證皮夾驗證端服務控制器
 */
@RestController
@RequestMapping("/api/twdiw/verify")
public class TWDIWVerifyController {
    
    @Autowired
    private TWDIWVerifyService verifyService;
    
    /**
     * 產生授權請求 QR 碼
     * 
     * @param ref 證件類型
     * @param transactionId 交易 ID，如果不提供則自動生成
     * @return QR碼回應
     */
    @GetMapping("/qrcode")
    public ResponseEntity<QrcodeResponseDto> generateQrcode(
            @RequestParam("ref") String ref,
            @RequestParam(value = "transaction_id", required = false) String transactionId) {
        
        QrcodeResponseDto response;
        if (transactionId != null && !transactionId.isEmpty()) {
            response = verifyService.generateQrcode(ref, transactionId);
        } else {
            response = verifyService.generateQrcode(ref);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 查詢VP展示驗證結果
     * 
     * @param transactionId 交易 ID
     * @param responseCode 回應碼（選填）
     * @return 驗證結果
     */
    @GetMapping("/result")
    public ResponseEntity<VerifyResultDto> getVerifyResult(
            @RequestParam("transaction_id") String transactionId,
            @RequestParam(value = "response_code", required = false) String responseCode) {
        
        VerifyResultDto result = verifyService.getVerifyResult(transactionId, responseCode);
        return ResponseEntity.ok(result);
    }
} 
