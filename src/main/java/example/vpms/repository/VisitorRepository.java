package example.vpms.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import example.vpms.model.Visitor;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    
    // 根據時間區間查詢訪客
    @Query("SELECT v FROM Visitor v WHERE v.checkInTime BETWEEN :startTime AND :endTime ORDER BY v.checkInTime DESC")
    List<Visitor> findByCheckInTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 查詢特定日期的訪客
    @Query("SELECT v FROM Visitor v WHERE DATE(v.checkInTime) = DATE(:date) ORDER BY v.checkInTime DESC")
    List<Visitor> findByCheckInDate(@Param("date") LocalDateTime date);
    
    // 根據公司名稱查詢訪客
    List<Visitor> findByCompanyNameContainingIgnoreCase(String companyName);
    
    // 根據姓名查詢訪客
    List<Visitor> findByNameContainingIgnoreCase(String name);
    
    // 根據電話查詢訪客
    List<Visitor> findByPhoneContaining(String phone);
} 
