package example.vpms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 訪客管理系統主頁控制器
 */
@Controller
public class QRCodeController {
    
    /**
     * 顯示訪客管理系統主頁
     */
    @GetMapping("/")
    public String showHomePage(Model model) {
        return "qrcode/scan";
    }
    
    /**
     * 顯示QR碼掃描頁面，現在重定向到主頁
     */
    @GetMapping("/scan")
    public String showQRCodePage() {
        return "redirect:/";
    }
} 
