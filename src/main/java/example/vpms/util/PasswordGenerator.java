package example.vpms.util;

import java.util.Scanner;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("請輸入要加密的密碼：");
        String rawPassword = scanner.nextLine();
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        
        System.out.println("\n原始密碼: " + rawPassword);
        System.out.println("加密後的密碼: " + encodedPassword);
        
        // 驗證
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("驗證結果: " + matches);
        
        scanner.close();
    }
} 
