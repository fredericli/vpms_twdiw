package example.vpms.config;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import example.vpms.model.Role;
import example.vpms.model.User;
import example.vpms.repository.UserRepository;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_TIME_MINUTES = 30;
    
    // 使用簡單的緩存來跟踪正在處理的認證請求
    private final ConcurrentHashMap<String, LocalDateTime> processingAuthentications = new ConcurrentHashMap<>();
    private static final long PROCESSING_TIMEOUT_SECONDS = 10; // 10秒後清除過期的處理請求
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public CustomAuthenticationProvider(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication == null) {
            return null;
        }
        
        String username = authentication.getName();
        Object credentials = authentication.getCredentials();
        
        if (username == null || username.trim().isEmpty() || credentials == null) {
            throw new BadCredentialsException("認證資訊不完整");
        }
        
        // 檢查是否正在處理同一用戶的認證請求
        LocalDateTime processingTime = processingAuthentications.get(username);
        if (processingTime != null) {
            // 如果處理時間超過超時時間，則清除紀錄
            if (processingTime.plusSeconds(PROCESSING_TIMEOUT_SECONDS).isBefore(LocalDateTime.now())) {
                processingAuthentications.remove(username);
            } else {
                // 正在處理中，直接返回已有的認證
                logger.debug("用戶 {} 的認證請求正在處理中，避免重複處理", username);
                Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
                if (existingAuth != null && existingAuth.isAuthenticated() && 
                    username.equals(existingAuth.getName())) {
                    return existingAuth;
                }
                // 如果沒有已有認證，返回 null，Spring Security 會將其視為認證失敗
                return null;
            }
        }
        
        // 標記此用戶的認證請求正在處理中
        processingAuthentications.put(username, LocalDateTime.now());
        
        try {
            String password = credentials.toString();
            
            logger.debug("嘗試認證使用者: {}", username);
            
            // 檢查當前安全上下文中是否已經包含了完全相同用戶名的認證信息
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            if (existingAuth != null && existingAuth.isAuthenticated() && 
                username.equals(existingAuth.getName()) &&
                existingAuth instanceof UsernamePasswordAuthenticationToken) {
                logger.debug("使用者 {} 已經通過認證，直接返回現有的認證信息", username);
                return existingAuth;
            }
            
            // 從資料庫獲取用戶
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        logger.warn("使用者不存在: {}", username);
                        return new BadCredentialsException("使用者不存在");
                    });
            
            // 檢查帳號是否被鎖定
            if (user.isAccountLocked()) {
                long minutesLeft = ChronoUnit.MINUTES.between(LocalDateTime.now(), user.getAccountLockedUntil());
                logger.warn("帳號已被鎖定，還剩 {} 分鐘解鎖: {}", minutesLeft, username);
                throw new LockedException("帳號已被鎖定，請 " + minutesLeft + " 分鐘後再試");
            }
            
            // 檢查密碼是否正確
            if (!passwordEncoder.matches(password, user.getPassword())) {
                user.incrementLoginAttempts();
                userRepository.save(user);
                
                if (user.getLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
                    user.lockAccount(LOCK_TIME_MINUTES);
                    userRepository.save(user);
                    logger.warn("帳號因多次登入失敗被鎖定: {}", username);
                    throw new LockedException("帳號因多次登入失敗被鎖定，請 " + LOCK_TIME_MINUTES + " 分鐘後再試");
                }
                
                logger.warn("密碼錯誤，剩餘嘗試次數: {}", MAX_LOGIN_ATTEMPTS - user.getLoginAttempts());
                throw new BadCredentialsException("密碼錯誤，剩餘嘗試次數: " + (MAX_LOGIN_ATTEMPTS - user.getLoginAttempts()));
            }
            
            // 登入成功，重置登入嘗試次數
            user.resetLoginAttempts();
            userRepository.save(user);
            
            // 設置用戶權限
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            for (Role role : user.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
                logger.debug("使用者 {} 擁有角色: {}", username, role.getName());
            }
            
            logger.info("使用者登入成功: {}", username);
            
            // 創建新的認證令牌，不保留密碼
            UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                    username,
                    null, 
                    authorities
            );
            
            // 添加用戶詳情
            result.setDetails(authentication.getDetails());
            
            return result;
        } finally {
            // 清除處理標記
            processingAuthentications.remove(username);
        }
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
} 
