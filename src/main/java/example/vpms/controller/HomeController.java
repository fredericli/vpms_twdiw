package example.vpms.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 訪客管理系統主頁控制器
 * 整合了 QR 碼掃描和訪客登入相關功能
 */
@Controller
public class HomeController {
    
    @Value("${server.servlet.context-path}")
    private String contextPath;
    
    /**
     * 顯示訪客管理系統主頁
     */
    @GetMapping("/")
    public String showHomePage(Model model) {
        model.addAttribute("contextPath", contextPath);
        return "qrcode/scan";
    }
    
    /**
     * 顯示QR碼掃描頁面，現在重定向到主頁
     */
    @GetMapping("/scan")
    public String showQRCodePage() {
        return "redirect:/";
    }
    
    /**
     * 顯示登入頁面
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
    
    /**
     * 將 /console 路徑重定向到登入頁面
     */
    @GetMapping("/console")
    public String redirectToLogin() {
        return "redirect:/login";
    }
} 