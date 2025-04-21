package example.vpms.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import example.vpms.dto.VisitorCardDto;
import example.vpms.twdiw.issuer.dto.CreateVCItemDataDTO;
import example.vpms.twdiw.issuer.dto.CreateVCItemDataField;
import example.vpms.twdiw.issuer.dto.VCItemDataDTO;
import example.vpms.twdiw.issuer.service.IssuerApiService;

/**
 * 訪客領卡服務
 */
@Service
public class VisitorCardService {
    
    private static final Logger logger = LoggerFactory.getLogger(VisitorCardService.class);
    
    private final IssuerApiService issuerApiService;
    
    @Value("${twdiw.issuer.card.vc-id}")
    private Integer vcId;
    
    @Value("${twdiw.issuer.card.vc-cid}")
    private String vcCid;
    
    @Autowired
    public VisitorCardService(IssuerApiService issuerApiService) {
        this.issuerApiService = issuerApiService;
    }
    
    /**
     * 處理訪客領卡請求並發行 VC
     * 
     * @param visitorCardDto 訪客領卡資料
     * @return VC 項目資料的回應
     */
    public VCItemDataDTO processVisitorCard(VisitorCardDto visitorCardDto) {
        logger.info("處理訪客領卡請求: {}", visitorCardDto);
        
        // 創建 VC 領卡資料
        CreateVCItemDataDTO createVCItemDataDTO = createVCItemData(visitorCardDto);
        
        // 調用 IssuerApiService 發行 VC
        try {
            VCItemDataDTO vcItemDataDTO = issuerApiService.setVCItemData(createVCItemDataDTO);
            logger.info("VC 發行成功，卡片 ID: {}, 二維碼: {}", 
                    vcItemDataDTO.getId(), 
                    vcItemDataDTO.getQrCode() != null ? "已生成" : "未生成");
            return vcItemDataDTO;
        } catch (Exception e) {
            logger.error("VC 發行失敗", e);
            throw e;
        }
    }
    
    /**
     * 創建 VC 項目資料的請求物件
     * 
     * @param visitorCardDto 訪客領卡資料
     * @return CreateVCItemDataDTO 請求物件
     */
    private CreateVCItemDataDTO createVCItemData(VisitorCardDto visitorCardDto) {
        CreateVCItemDataDTO dto = new CreateVCItemDataDTO();
        dto.setVcId(vcId);
        dto.setVcCid(vcCid);
        
        List<CreateVCItemDataField> fields = new ArrayList<>();
        
        // 添加公司名稱欄位 - 處理空值
        CreateVCItemDataField companyField = new CreateVCItemDataField();
        companyField.setType("CUSTOM");
        companyField.setCname("公司名稱");
        companyField.setEname("company");
        companyField.setContent(visitorCardDto.getCompanyName() != null ? visitorCardDto.getCompanyName() : "-");
        fields.add(companyField);
        
        // 添加姓名欄位 - 處理空值，保留空格
        CreateVCItemDataField nameField = new CreateVCItemDataField();
        nameField.setType("BASIC");
        nameField.setCname("姓名");
        nameField.setEname("name");
        // 如果名字為null或空字串則顯示-，否則保留原始輸入（包含可能的空格）
        String nameValue = (visitorCardDto.getName() == null || visitorCardDto.getName().isEmpty()) 
                          ? "-" 
                          : visitorCardDto.getName();
        nameField.setContent(nameValue);
        fields.add(nameField);
        
        // 添加電子郵件欄位
        CreateVCItemDataField emailField = new CreateVCItemDataField();
        emailField.setType("CUSTOM");
        emailField.setCname("電子郵件");
        emailField.setEname("email");
        emailField.setContent(visitorCardDto.getEmail());
        fields.add(emailField);
        
        dto.setFields(fields);
        
        return dto;
    }
} 
