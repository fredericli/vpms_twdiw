package example.vpms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import example.vpms.model.VCApply;

/**
 * 訪客領卡申請資料表 Repository
 */
@Repository
public interface VCApplyRepository extends JpaRepository<VCApply, Long> {
    
    /**
     * 根據交易識別碼查詢申請資料
     * 
     * @param transactionId 交易識別碼
     * @return 申請資料
     */
    Optional<VCApply> findByTransactionId(String transactionId);
    
    /**
     * 根據身分證字號查詢申請資料
     * 
     * @param idNumber 身分證字號
     * @return 申請資料
     */
    Optional<VCApply> findByIdNumber(String idNumber);
    
    /**
     * 根據手機號碼查詢申請資料
     * 
     * @param cellphone 手機號碼
     * @return 申請資料
     */
    Optional<VCApply> findByCellphone(String cellphone);
    
    /**
     * 根據電子郵件查詢申請資料
     * 
     * @param email 電子郵件
     * @return 申請資料
     */
    Optional<VCApply> findByEmail(String email);
    
    /**
     * 根據電子郵件和狀態查詢所有申請資料
     * 
     * @param email 電子郵件
     * @param status 狀態
     * @return 申請資料列表
     */
    List<VCApply> findAllByEmailAndStatus(String email, String status);
} 