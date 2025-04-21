package example.vpms.twdiw.issuer.dto;

/**
 * VC 項目資料的回應 DTO
 */
public class VCItemDataDTO {
    
    private Integer id;
    
    private String content;
    
    private String pureContent;
    
    private Integer crUser;
    
    private String crDatetime;
    
    private String issuanceDate;
    
    private Integer valid;
    
    private String clearScheduleId;
    
    private String clearScheduleDatetime;
    
    private String vcCid;
    
    private String transactionId;
    
    private String businessId;
    
    private String vcItemName;
    
    private String qrCode;
    
    private String deepLink;
    
    private String expired;
    
    private String scheduleRevokeMessage;
    
    private VCItemDTO vcItem;
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getPureContent() {
        return pureContent;
    }
    
    public void setPureContent(String pureContent) {
        this.pureContent = pureContent;
    }
    
    public Integer getCrUser() {
        return crUser;
    }
    
    public void setCrUser(Integer crUser) {
        this.crUser = crUser;
    }
    
    public String getCrDatetime() {
        return crDatetime;
    }
    
    public void setCrDatetime(String crDatetime) {
        this.crDatetime = crDatetime;
    }
    
    public String getIssuanceDate() {
        return issuanceDate;
    }
    
    public void setIssuanceDate(String issuanceDate) {
        this.issuanceDate = issuanceDate;
    }
    
    public Integer getValid() {
        return valid;
    }
    
    public void setValid(Integer valid) {
        this.valid = valid;
    }
    
    public String getClearScheduleId() {
        return clearScheduleId;
    }
    
    public void setClearScheduleId(String clearScheduleId) {
        this.clearScheduleId = clearScheduleId;
    }
    
    public String getClearScheduleDatetime() {
        return clearScheduleDatetime;
    }
    
    public void setClearScheduleDatetime(String clearScheduleDatetime) {
        this.clearScheduleDatetime = clearScheduleDatetime;
    }
    
    public String getVcCid() {
        return vcCid;
    }
    
    public void setVcCid(String vcCid) {
        this.vcCid = vcCid;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getBusinessId() {
        return businessId;
    }
    
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
    
    public String getVcItemName() {
        return vcItemName;
    }
    
    public void setVcItemName(String vcItemName) {
        this.vcItemName = vcItemName;
    }
    
    public String getQrCode() {
        return qrCode;
    }
    
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
    
    public String getDeepLink() {
        return deepLink;
    }
    
    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }
    
    public String getExpired() {
        return expired;
    }
    
    public void setExpired(String expired) {
        this.expired = expired;
    }
    
    public String getScheduleRevokeMessage() {
        return scheduleRevokeMessage;
    }
    
    public void setScheduleRevokeMessage(String scheduleRevokeMessage) {
        this.scheduleRevokeMessage = scheduleRevokeMessage;
    }
    
    public VCItemDTO getVcItem() {
        return vcItem;
    }
    
    public void setVcItem(VCItemDTO vcItem) {
        this.vcItem = vcItem;
    }
} 
