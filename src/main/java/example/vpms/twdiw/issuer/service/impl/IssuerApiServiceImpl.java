package example.vpms.twdiw.issuer.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import example.vpms.twdiw.issuer.dto.CreateVCItemDataDTO;
import example.vpms.twdiw.issuer.dto.DwIssuerVC402iResDTO;
import example.vpms.twdiw.issuer.dto.DwissuerVC203iResp;
import example.vpms.twdiw.issuer.dto.VCItemDataDTO;
import example.vpms.twdiw.issuer.service.IssuerApiService;

/**
 * 發行端 API 服務實現
 */
@Service
public class IssuerApiServiceImpl implements IssuerApiService {
    
    private final RestTemplate restTemplate;
    
    @Value("${twdiw.issuer.api.base-url:https://issuer-sandbox.wallet.gov.tw}")
    private String baseUrl;
    
    @Value("${twdiw.issuer.api.access-token}")
    private String accessToken;
    
    public IssuerApiServiceImpl(@Qualifier("issuerApiRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", accessToken);
        return headers;
    }
    
    @Override
    public VCItemDataDTO setVCItemData(CreateVCItemDataDTO createVCItemDataDTO) {
        String url = baseUrl + "/api/qrcode/data";
        
        HttpEntity<CreateVCItemDataDTO> requestEntity = new HttpEntity<>(createVCItemDataDTO, createHeaders());
        
        ResponseEntity<VCItemDataDTO> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                VCItemDataDTO.class
        );
        
        return responseEntity.getBody();
    }
    
    @Override
    public DwIssuerVC402iResDTO getVCItemDataByNonce(String nonceId) {
        String url = baseUrl + "/api/credential/nonce/" + nonceId;
        
        HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
        
        ResponseEntity<DwIssuerVC402iResDTO> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                DwIssuerVC402iResDTO.class
        );
        
        return responseEntity.getBody();
    }
    
    @Override
    public DwissuerVC203iResp vCItemDataRevoke(String cid, String action) {
        String url = baseUrl + "/api/credential/" + cid + "/" + action;
        
        HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
        
        ResponseEntity<DwissuerVC203iResp> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                requestEntity,
                DwissuerVC203iResp.class
        );
        
        return responseEntity.getBody();
    }
} 
