package example.vpms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import example.vpms.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 根據使用者名稱查詢使用者
    Optional<User> findByUsername(String username);
    
    // 檢查使用者名稱是否存在
    boolean existsByUsername(String username);
    
    // 檢查電子郵件是否存在
    boolean existsByEmail(String email);
    
    // 根據電子郵件查詢使用者
    Optional<User> findByEmail(String email);
} 
