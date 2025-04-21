package example.vpms.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import example.vpms.model.Recipient;
import example.vpms.service.EmailService;
import example.vpms.service.RecipientService;
import example.vpms.service.VisitorService;

@Component
public class DailyReportScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(DailyReportScheduler.class);
    
    private final VisitorService visitorService;
    private final EmailService emailService;
    private final RecipientService recipientService;
    
    @Autowired
    public DailyReportScheduler(VisitorService visitorService, EmailService emailService, RecipientService recipientService) {
        this.visitorService = visitorService;
        this.emailService = emailService;
        this.recipientService = recipientService;
    }
    
    /**
     * 每日 01:00 執行，發送前一天的訪客日報
     */
    // @Scheduled(cron = "0 0 1 * * ?")
    public void sendDailyReport() {
        logger.info("開始執行每日訪客報告排程任務");
        
        try {
            // 獲取前一天的日期
            LocalDate yesterday = LocalDate.now().minusDays(1);
            LocalDateTime reportDate = LocalDateTime.of(yesterday, LocalTime.of(0, 0, 0));
            
            // 生成報告內容
            String reportContent = visitorService.generateDailyReport(reportDate);
            
            // 獲取所有啟用的收件人
            List<Recipient> recipients = recipientService.findAllEnabled();
            if (recipients.isEmpty()) {
                logger.warn("沒有啟用的收件人，無法發送報告");
                return;
            }
            
            // 轉換收件人列表為郵件地址列表
            List<String> emailAddresses = recipients.stream()
                    .map(Recipient::getEmail)
                    .collect(Collectors.toList());
            
            // 發送報告
            emailService.sendDailyVisitorReport(emailAddresses, reportContent, reportDate);
            
            logger.info("每日訪客報告發送成功，收件人數量: {}", emailAddresses.size());
        } catch (Exception e) {
            logger.error("發送每日訪客報告時發生錯誤", e);
        }
    }
} 
