package example.vpms.twdiw.issuer.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * 建立 VC 項目資料的 DTO
 */
public class CreateVCItemDataDTO {
    
    @NotNull
    private Integer vcId;
    
    @NotBlank
    private String vcCid;
    
    @NotEmpty
    private List<CreateVCItemDataField> fields;
    
    public Integer getVcId() {
        return vcId;
    }
    
    public void setVcId(Integer vcId) {
        this.vcId = vcId;
    }
    
    public String getVcCid() {
        return vcCid;
    }
    
    public void setVcCid(String vcCid) {
        this.vcCid = vcCid;
    }
    
    public List<CreateVCItemDataField> getFields() {
        return fields;
    }
    
    public void setFields(List<CreateVCItemDataField> fields) {
        this.fields = fields;
    }
} 