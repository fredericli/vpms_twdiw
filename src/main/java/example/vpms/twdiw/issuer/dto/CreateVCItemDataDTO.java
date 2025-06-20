package example.vpms.twdiw.issuer.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

/**
 * 建立 VC 項目資料的 DTO
 */
public class CreateVCItemDataDTO {
    
    private String vcUid;

    @NotEmpty
    private List<CreateVCItemDataField> fields;

    public String getVcUid() {
        return vcUid;
    }

    public void setVcUid(String vcUid) {
        this.vcUid = vcUid;
    }

    public List<CreateVCItemDataField> getFields() {
        return fields;
    }
    
    public void setFields(List<CreateVCItemDataField> fields) {
        this.fields = fields;
    }
} 