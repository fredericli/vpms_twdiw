package example.vpms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import example.vpms.model.Recipient;

@Repository
public interface RecipientRepository extends JpaRepository<Recipient, Long> {
    
    // 查詢啟用的收件人列表
    @Query("SELECT r FROM Recipient r WHERE r.enabled = true")
    List<Recipient> findAllEnabled();
    
    // 根據電子郵件查詢收件人
    Optional<Recipient> findByEmail(String email);
    
    // 根據姓名查詢收件人
    List<Recipient> findByNameContainingIgnoreCase(String name);
} 
