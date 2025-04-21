package example.vpms.twdiw.verify.example;

import java.util.Scanner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import example.vpms.twdiw.verify.dto.QrcodeResponseDto;
import example.vpms.twdiw.verify.dto.VerifyResultDto;
import example.vpms.twdiw.verify.service.TWDIWVerifyService;

/**
 * 數位憑證皮夾驗證端服務使用範例
 * 這個類是為了展示如何使用 TWDIWVerifyService，實際應用中可能不需要這個類
 */
//@Component  // 如果需要自動執行這個範例，請取消此註釋
public class TWDIWVerifyExample implements CommandLineRunner {
    
    @Autowired
    private TWDIWVerifyService verifyService;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== 數位憑證皮夾驗證端服務範例 ===");
        System.out.println("1. 產生授權請求 QR 碼");
        System.out.println("2. 查詢 VP 展示驗證結果");
        System.out.print("請選擇功能 (1-2): ");
        
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        scanner.nextLine(); // 清空輸入緩衝區
        
        switch (choice) {
            case 1:
                generateQrcode(scanner);
                break;
            case 2:
                queryVerifyResult(scanner);
                break;
            default:
                System.out.println("無效的選擇！");
        }
    }
    
    private void generateQrcode(Scanner scanner) {
        System.out.print("請輸入證件類型 (ref): ");
        String ref = scanner.nextLine();
        
        String transactionId = UUID.randomUUID().toString();
        System.out.println("使用交易ID: " + transactionId);
        
        try {
            QrcodeResponseDto response = verifyService.generateQrcode(ref, transactionId);
            
            System.out.println("QR 碼產生結果:");
            System.out.println("代碼: " + response.getCode());
            System.out.println("訊息: " + response.getMessage());
            
            if (response.getData() != null) {
                System.out.println("授權 URI: " + response.getData().getAuthUri());
                System.out.println("QR 碼 (Base64): " + response.getData().getQrcode().substring(0, 50) + "...");
            }
            
            System.out.println("請記住交易ID，稍後用於查詢驗證結果: " + transactionId);
        } catch (Exception e) {
            System.err.println("發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void queryVerifyResult(Scanner scanner) {
        System.out.print("請輸入交易ID: ");
        String transactionId = scanner.nextLine();
        
        System.out.print("是否提供回應碼? (y/n): ");
        boolean provideResponseCode = scanner.nextLine().equalsIgnoreCase("y");
        
        String responseCode = null;
        if (provideResponseCode) {
            System.out.print("請輸入回應碼: ");
            responseCode = scanner.nextLine();
        }
        
        try {
            VerifyResultDto result = verifyService.getVerifyResult(transactionId, responseCode);
            
            System.out.println("驗證結果:");
            System.out.println("驗證結果: " + (result.getVerifyResult() ? "成功" : "失敗"));
            System.out.println("結果描述: " + result.getResultDescription());
            System.out.println("交易ID: " + result.getTransactionId());
            
            if (result.getData() != null && !result.getData().isEmpty()) {
                System.out.println("-----憑證資料-----");
                result.getData().forEach(credentialData -> {
                    System.out.println("憑證類型: " + credentialData.getCredentialType());
                    
                    if (credentialData.getClaims() != null && !credentialData.getClaims().isEmpty()) {
                        System.out.println("屬性:");
                        credentialData.getClaims().forEach(claim -> {
                            System.out.printf("  %s (%s): %s%n", 
                                claim.getCname(), 
                                claim.getEname(), 
                                claim.getValue());
                        });
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
