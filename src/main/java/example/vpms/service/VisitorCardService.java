package example.vpms.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import example.vpms.dto.VisitorCardDto;
import example.vpms.model.VCApply;
import example.vpms.repository.VCApplyRepository;
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
    private final VCApplyRepository vcApplyRepository;
    
    @Value("${twdiw.issuer.card.vcUid}")
    private String vcUid;
    
    @Autowired
    public VisitorCardService(IssuerApiService issuerApiService, VCApplyRepository vcApplyRepository) {
        this.issuerApiService = issuerApiService;
        this.vcApplyRepository = vcApplyRepository;
    }
    
    /**
     * 處理訪客領卡請求並發行 VC
     * 
     * @param visitorCardDto 訪客領卡資料
     * @return VC 項目資料的回應
     */
    @Transactional
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
            
            // 儲存申請資料
            saveVCApply(visitorCardDto, vcItemDataDTO);
            
            return vcItemDataDTO;
        } catch (Exception e) {
            logger.error("VC 發行失敗", e);
            throw e;
        }
    }
    
    /**
     * 儲存訪客領卡申請資料
     * 
     * @param visitorCardDto 訪客領卡資料
     * @param vcItemDataDTO VC 項目資料
     */
    private void saveVCApply(VisitorCardDto visitorCardDto, VCItemDataDTO vcItemDataDTO) {
        VCApply vcApply = new VCApply();
        vcApply.setName(visitorCardDto.getName());
        vcApply.setCellphone(visitorCardDto.getCellphone());
        vcApply.setEmail(visitorCardDto.getEmail());
        vcApply.setCompanyName(visitorCardDto.getCompanyName());
        vcApply.setJobTitle(visitorCardDto.getJobTitle());
        vcApply.setVcCid(vcUid);
        vcApply.setTransactionId(vcItemDataDTO.getTransactionId());
        vcApply.setQrCode(vcItemDataDTO.getQrCode());
        vcApply.setDeepLink(vcItemDataDTO.getDeepLink());
        vcApply.setCredential(vcItemDataDTO.getCredential());
        vcApply.setStatus("PENDING");
        vcApply.setCreatedAt(LocalDateTime.now());
        
        vcApplyRepository.save(vcApply);
        logger.info("訪客領卡申請資料已儲存，ID: {}", vcApply.getId());
    }
    
    /**
     * 創建 VC 項目資料的請求物件
     * 
     * @param visitorCardDto 訪客領卡資料
     * @return CreateVCItemDataDTO 請求物件
     */
    private CreateVCItemDataDTO createVCItemData(VisitorCardDto visitorCardDto) {
        CreateVCItemDataDTO createVCItemDataDTO = new CreateVCItemDataDTO();

        createVCItemDataDTO.setVcUid(vcUid);
        List<CreateVCItemDataField> fields = new ArrayList<>();

        // 公司或部門
        CreateVCItemDataField companyField = new CreateVCItemDataField();
        companyField.setEname("company");
        companyField.setContent(visitorCardDto.getCompanyName());
        fields.add(companyField);
        
        // 姓名
        CreateVCItemDataField nameField = new CreateVCItemDataField();
        nameField.setEname("name");
        nameField.setContent(visitorCardDto.getName());
        fields.add(nameField);
        
        // 電子郵件
        CreateVCItemDataField emailField = new CreateVCItemDataField();
        emailField.setEname("email");
        emailField.setContent(visitorCardDto.getEmail());
        fields.add(emailField);
        
        // 職稱
        CreateVCItemDataField jobTitleField = new CreateVCItemDataField();
        jobTitleField.setEname("job_title");
        jobTitleField.setContent(visitorCardDto.getJobTitle());
        fields.add(jobTitleField);        
        
        // 手機號碼
        CreateVCItemDataField cellphoneField = new CreateVCItemDataField();
        cellphoneField.setEname("cellphone");
        cellphoneField.setContent(visitorCardDto.getCellphone());
        fields.add(cellphoneField);
        
        createVCItemDataDTO.setFields(fields);
        
        return createVCItemDataDTO;
    }
} 
