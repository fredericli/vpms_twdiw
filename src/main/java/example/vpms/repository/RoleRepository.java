package example.vpms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import example.vpms.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    // 根據角色名稱查詢角色
    Optional<Role> findByName(String name);
} 
