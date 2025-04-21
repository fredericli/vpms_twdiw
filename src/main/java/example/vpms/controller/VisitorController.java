package example.vpms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import example.vpms.service.VisitorService;

@Controller
public class VisitorController {
    
    private final VisitorService visitorService;
    
    @Autowired
    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
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
