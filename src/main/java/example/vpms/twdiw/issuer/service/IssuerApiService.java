package example.vpms.twdiw.issuer.service;

import example.vpms.twdiw.issuer.dto.CreateVCItemDataDTO;
import example.vpms.twdiw.issuer.dto.DwIssuerVC402iResDTO;
import example.vpms.twdiw.issuer.dto.DwissuerVC203iResp;
import example.vpms.twdiw.issuer.dto.VCItemDataDTO;

/**
 * 發行端 API 服務接口
 */
public interface IssuerApiService {
    
    /**
     * 發行端服務接收 VC 實際資料內容及產生 QR Code
     *
     * @param createVCItemDataDTO VC 實際資料內容
     * @return VC 項目資料的回應 DTO
     */
    VCItemDataDTO setVCItemData(CreateVCItemDataDTO createVCItemDataDTO);
    
    /**
     * 發行端 VC 卡片單一資料查詢
     *
     * @param nonceId 發行端申請卡片時所產生的 nonce 唯一值
     * @return VC 卡片單一資料查詢回應 DTO
     */
    DwIssuerVC402iResDTO getVCItemDataByNonce(String nonceId);
    
    /**
     * 發行端 VC 卡片狀態變更
     *
     * @param cid VC卡片序號
     * @param action 操作類型（如：revocation）
     * @return VC 卡片狀態變更回應 DTO
     */
    DwissuerVC203iResp vCItemDataRevoke(String cid, String action);
} 