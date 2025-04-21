package example.vpms.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 搜尋參數資料傳輸物件，用於表單提交
 */
public class SearchDto {
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    
    private String keyword;
    
    // 默認建構函數
    public SearchDto() {
    }
    
    // 帶參數的建構函數
    public SearchDto(LocalDateTime startTime, LocalDateTime endTime, String keyword) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.keyword = keyword;
    }
    
    // Getter 和 Setter 方法
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    @Override
    public String toString() {
        return "SearchDto{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", keyword='" + keyword + '\'' +
                '}';
    }
} 
