package example.vpms.twdiw.issuer.dto;

/**
 * VC 項目資料欄位
 */
public class CreateVCItemDataField {
    
    private String type;
    
    private String cname;
    
    private String ename;
    
    private String content;
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getCname() {
        return cname;
    }
    
    public void setCname(String cname) {
        this.cname = cname;
    }
    
    public String getEname() {
        return ename;
    }
    
    public void setEname(String ename) {
        this.ename = ename;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
} 