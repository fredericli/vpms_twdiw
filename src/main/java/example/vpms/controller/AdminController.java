package example.vpms.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import example.vpms.dto.SearchDto;
import example.vpms.model.Recipient;
import example.vpms.model.Visitor;
import example.vpms.service.RecipientService;
import example.vpms.service.VisitorService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final VisitorService visitorService;
    private final RecipientService recipientService;
    
    @Autowired
    public AdminController(VisitorService visitorService, RecipientService recipientService) {
        this.visitorService = visitorService;
        this.recipientService = recipientService;
    }
    
    /**
     * 顯示管理員儀表板
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // 獲取當天的訪客數據
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        List<Visitor> todayVisitors = visitorService.findByTimeRange(startOfDay, endOfDay);
        
        // 獲取所有訪客數據
        List<Visitor> allVisitors = visitorService.findAll();
        
        // 設置搜尋的默認時間範圍：開始時間為當前時間往前一小時，結束時間為當前時間
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        SearchDto searchDto = new SearchDto(oneHourAgo, now, null);
        
        model.addAttribute("todayVisitors", todayVisitors);
        model.addAttribute("allVisitors", allVisitors);
        model.addAttribute("searchDto", searchDto);
        
        return "admin/dashboard";
    }
    
    /**
     * 根據時間範圍搜尋訪客
     */
    @PostMapping("/search")
    public String searchVisitors(@ModelAttribute SearchDto searchDto, Model model) {
        List<Visitor> visitors;
        
        // 確保開始時間小於結束時間
        if (searchDto.getStartTime() != null && searchDto.getEndTime() != null && 
            searchDto.getStartTime().isAfter(searchDto.getEndTime())) {
            // 如果開始時間大於結束時間，交換兩者
            LocalDateTime temp = searchDto.getStartTime();
            searchDto.setStartTime(searchDto.getEndTime());
            searchDto.setEndTime(temp);
        }
        
        if (searchDto.getStartTime() != null && searchDto.getEndTime() != null) {
            // 根據時間範圍搜尋
            visitors = visitorService.findByTimeRange(searchDto.getStartTime(), searchDto.getEndTime());
        } else if (searchDto.getKeyword() != null && !searchDto.getKeyword().trim().isEmpty()) {
            // 根據關鍵字搜尋
            visitors = visitorService.searchVisitors(searchDto.getKeyword());
        } else {
            // 獲取所有訪客
            visitors = visitorService.findAll();
        }
        
        model.addAttribute("visitors", visitors);
        model.addAttribute("searchDto", searchDto);
        
        return "admin/search_results";
    }
    
    /**
     * 顯示收件人管理頁面
     */
    @GetMapping("/recipients")
    public String showRecipients(Model model) {
        List<Recipient> recipients = recipientService.findAll();
        model.addAttribute("recipients", recipients);
        model.addAttribute("recipient", new Recipient());
        
        return "admin/recipients";
    }
    
    /**
     * 新增收件人
     */
    @PostMapping("/recipients/add")
    public String addRecipient(@Valid @ModelAttribute("recipient") Recipient recipient,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/recipients";
        }
        
        recipientService.addRecipient(recipient);
        redirectAttributes.addFlashAttribute("successMessage", "收件人新增成功");
        
        return "redirect:/admin/recipients";
    }
    
    /**
     * 啟用或禁用收件人
     */
    @PostMapping("/recipients/{id}/toggle")
    public String toggleRecipientStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        recipientService.toggleEnabled(id, enabled);
        return "redirect:/admin/recipients";
    }
    
    /**
     * 刪除收件人
     */
    @PostMapping("/recipients/{id}/delete")
    public String deleteRecipient(@PathVariable Long id) {
        recipientService.deleteById(id);
        return "redirect:/admin/recipients";
    }
    
    /**
     * 顯示訪客詳情頁面
     */
    @GetMapping("/visitors/{id}")
    public String showVisitorDetails(@PathVariable Long id, Model model) {
        Optional<Visitor> visitorOpt = visitorService.findById(id);
        if (visitorOpt.isPresent()) {
            model.addAttribute("visitor", visitorOpt.get());
            return "admin/visitor_details";
        } else {
            return "redirect:/admin/dashboard";
        }
    }
    
    /**
     * 手動生成特定日期的報告
     */
    @GetMapping("/report")
    public String generateReport(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Model model) {
        LocalDateTime reportDate = LocalDateTime.of(date, LocalTime.MIN);
        String report = visitorService.generateDailyReport(reportDate);
        
        model.addAttribute("report", report);
        model.addAttribute("date", date);
        
        return "admin/report";
    }
} 
