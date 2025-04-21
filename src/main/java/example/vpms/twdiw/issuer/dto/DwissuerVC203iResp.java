package example.vpms.twdiw.issuer.dto;

/**
 * 發行端 VC 卡片狀態變更回應 DTO
 */
public class DwissuerVC203iResp {
    
    private String credentialStatus;

    public String getCredentialStatus() {
        return credentialStatus;
    }

    public void setCredentialStatus(String credentialStatus) {
        this.credentialStatus = credentialStatus;
    }
} 