package example.vpms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 訪客領卡資料傳輸物件
 */
public class VisitorCardDto {
    
    @NotBlank(message = "公司或部門名稱不能為空")
    @Size(max = 255, message = "公司或部門名稱不能超過255個字符")
    private String companyName;
    
    @NotBlank(message = "姓名不能為空")
    @Size(max = 255, message = "姓名不能超過255個字符")
    private String name;
    
    @NotBlank(message = "電子郵件不能為空")
    @Email(message = "電子郵件格式不正確")
    @Size(max = 255, message = "電子郵件不能超過255個字符")
    private String email;
    
    @NotBlank(message = "職稱不能為空")
    @Size(max = 255, message = "職稱不能超過255個字符")
    private String jobTitle;

    @NotBlank(message = "手機號碼不能為空")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    @Size(max = 20, message = "手機號碼不能超過20個字符")
    private String cellphone;
    
    // 默認建構函數
    public VisitorCardDto() {
    }
    
    // 帶參數的建構函數
    public VisitorCardDto(String companyName, String name, String email, String jobTitle, String cellphone) {
        this.companyName = companyName;
        this.name = name;
        this.email = email;
        this.jobTitle = jobTitle;
        this.cellphone = cellphone;
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
    
    public String getJobTitle() {
        return jobTitle;
    }
    
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCellphone() {
        return cellphone;
    }
    
    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }
    
    @Override
    public String toString() {
        return "VisitorCardDto{" +
                "companyName='" + companyName + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", cellphone='" + cellphone + '\'' +
                '}';
    }
} 