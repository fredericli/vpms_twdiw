package example.vpms.twdiw.verify.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 驗證端查詢VP展示驗證結果的響應 DTO
 */
public class VerifyResultDto {
    
    @JsonProperty("verify_result")
    private Boolean verifyResult;
    
    @JsonProperty("result_description")
    private String resultDescription;
    
    @JsonProperty("transaction_id")
    private String transactionId;
    
    private List<CredentialData> data;
    
    public Boolean getVerifyResult() {
        return verifyResult;
    }
    
    public void setVerifyResult(Boolean verifyResult) {
        this.verifyResult = verifyResult;
    }
    
    public String getResultDescription() {
        return resultDescription;
    }
    
    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public List<CredentialData> getData() {
        return data;
    }
    
    public void setData(List<CredentialData> data) {
        this.data = data;
    }
    
    public static class CredentialData {
        
        @JsonProperty("credentialType")
        private String credentialType;
        
        private List<Claim> claims;
        
        public String getCredentialType() {
            return credentialType;
        }
        
        public void setCredentialType(String credentialType) {
            this.credentialType = credentialType;
        }
        
        public List<Claim> getClaims() {
            return claims;
        }
        
        public void setClaims(List<Claim> claims) {
            this.claims = claims;
        }
    }
    
    public static class Claim {
        
        private String ename;
        private String cname;
        private String value;
        
        public String getEname() {
            return ename;
        }
        
        public void setEname(String ename) {
            this.ename = ename;
        }
        
        public String getCname() {
            return cname;
        }
        
        public void setCname(String cname) {
            this.cname = cname;
        }
        
        public String getValue() {
            return value;
        }
        
        public void setValue(String value) {
            this.value = value;
        }
    }
} 
