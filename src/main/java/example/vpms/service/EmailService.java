package example.vpms.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * 電子郵件服務
 */
@Service
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    /**
     * 發送訪客日報郵件
     */
    public void sendDailyVisitorReport(List<String> recipients, String reportContent, LocalDateTime reportDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = reportDate.format(formatter);
        String subject = "訪客日報表 - " + formattedDate;
        
        sendEmailToMultipleRecipients(recipients, subject, reportContent);
    }
    
    /**
     * 發送一般郵件
     */
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    
    /**
     * 發送HTML格式郵件
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("發送HTML郵件失敗", e);
        }
    }
    
    /**
     * 批量發送郵件
     */
    public void sendEmailToMultipleRecipients(List<String> recipients, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipients.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
} 
