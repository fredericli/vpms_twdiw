package example.vpms.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import example.vpms.model.Visitor;
import example.vpms.repository.VisitorRepository;

@Service
public class VisitorService {
    
    private final VisitorRepository visitorRepository;
    
    @Autowired
    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }
    
    /**
     * 登記新訪客
     */
    public Visitor registerVisitor(Visitor visitor) {
        if (visitor.getCheckInTime() == null) {
            visitor.setCheckInTime(LocalDateTime.now());
        }
        if (visitor.getCreatedDate() == null) {
            visitor.setCreatedDate(LocalDateTime.now());
        }
        return visitorRepository.save(visitor);
    }
    
    /**
     * 根據ID查詢訪客
     */
    public Optional<Visitor> findById(Long id) {
        return visitorRepository.findById(id);
    }
    
    /**
     * 獲取所有訪客
     */
    public List<Visitor> findAll() {
        return visitorRepository.findAll();
    }
    
    /**
     * 根據時間區間查詢訪客
     */
    public List<Visitor> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return visitorRepository.findByCheckInTimeBetween(startTime, endTime);
    }
    
    /**
     * 查詢特定日期的訪客
     */
    public List<Visitor> findByDate(LocalDateTime date) {
        return visitorRepository.findByCheckInDate(date);
    }
    
    /**
     * 根據訪客資訊查詢
     */
    public List<Visitor> searchVisitors(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return visitorRepository.findAll();
        }
        
        // 合併多種搜尋結果
        List<Visitor> result = new ArrayList<>();
        result.addAll(visitorRepository.findByCompanyNameContainingIgnoreCase(keyword));
        result.addAll(visitorRepository.findByNameContainingIgnoreCase(keyword));
        result.addAll(visitorRepository.findByPhoneContaining(keyword));
        
        // 去除重複
        return result.stream().distinct().toList();
    }
    
    /**
     * 獲取指定日期的訪客報告
     */
    public String generateDailyReport(LocalDateTime date) {
        List<Visitor> visitors = findByDate(date);
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String reportDate = date.format(dateFormatter);
        
        StringBuilder report = new StringBuilder();
        report.append("訪客日報表 - ").append(reportDate).append("\n\n");
        report.append("總訪客數量: ").append(visitors.size()).append("\n\n");
        report.append("訪客詳情:\n");
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        for (int i = 0; i < visitors.size(); i++) {
            Visitor visitor = visitors.get(i);
            report.append(i + 1).append(". ");
            report.append("簽到時間: ").append(visitor.getCheckInTime().format(timeFormatter)).append(", ");
            report.append("公司: ").append(visitor.getCompanyName()).append(", ");
            report.append("姓名: ").append(visitor.getName()).append(", ");
            report.append("電話: ").append(visitor.getPhone()).append("\n");
        }
        
        return report.toString();
    }
} 
