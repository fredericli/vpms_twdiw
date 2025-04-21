package example.vpms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 訪客領卡資料傳輸物件
 */
public class VisitorCardDto {
    
    @Size(max = 255, message = "公司名稱不能超過255個字符")
    private String companyName;
    
    @Size(max = 255, message = "姓名不能超過255個字符")
    private String name;
    
    @NotBlank(message = "電子郵件不能為空")
    @Email(message = "請提供有效的電子郵件地址")
    @Size(max = 255, message = "電子郵件不能超過255個字符")
    private String email;
    
    // 默認建構函數
    public VisitorCardDto() {
    }
    
    // 帶參數的建構函數
    public VisitorCardDto(String companyName, String name, String email) {
        this.companyName = companyName;
        this.name = name;
        this.email = email;
    }
    
    // Getter 和 Setter 方法
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
        return "VisitorCardDto{" +
                "companyName='" + companyName + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
} 