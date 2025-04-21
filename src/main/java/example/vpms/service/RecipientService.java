package example.vpms.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import example.vpms.model.Recipient;
import example.vpms.repository.RecipientRepository;

/**
 * 收件人服務
 */
@Service
public class RecipientService {

    private final RecipientRepository recipientRepository;

    @Autowired
    public RecipientService(RecipientRepository recipientRepository) {
        this.recipientRepository = recipientRepository;
    }

    /**
     * 新增收件人
     */
    public Recipient addRecipient(Recipient recipient) {
        if (recipient.getCreatedDate() == null) {
            recipient.setCreatedDate(LocalDateTime.now());
        }
        return recipientRepository.save(recipient);
    }

    /**
     * 修改收件人
     */
    public Recipient updateRecipient(Recipient recipient) {
        return recipientRepository.save(recipient);
    }

    /**
     * 根據ID查詢收件人
     */
    public Optional<Recipient> findById(Long id) {
        return recipientRepository.findById(id);
    }

    /**
     * 獲取所有收件人
     */
    public List<Recipient> findAll() {
        return recipientRepository.findAll();
    }

    /**
     * 獲取所有啟用的收件人
     */
    public List<Recipient> findAllEnabled() {
        return recipientRepository.findAllEnabled();
    }

    /**
     * 根據ID刪除收件人
     */
    public void deleteById(Long id) {
        recipientRepository.deleteById(id);
    }

    /**
     * 啟用或禁用收件人
     */
    public Recipient toggleEnabled(Long id, boolean enabled) {
        Optional<Recipient> optionalRecipient = recipientRepository.findById(id);
        if (optionalRecipient.isPresent()) {
            Recipient recipient = optionalRecipient.get();
            recipient.setEnabled(enabled);
            return recipientRepository.save(recipient);
        }
        throw new IllegalArgumentException("收件人不存在，ID: " + id);
    }

    /**
     * 根據名稱或郵箱搜尋收件人
     */
    public List<Recipient> searchRecipients(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return recipientRepository.findAll();
        }

        // 先檢查精確的email查詢
        Optional<Recipient> byEmail = recipientRepository.findByEmail(keyword);
        if (byEmail.isPresent()) {
            List<Recipient> result = new ArrayList<>();
            result.add(byEmail.get());
            return result;
        }

        // 模糊搜尋名稱
        return recipientRepository.findByNameContainingIgnoreCase(keyword);
    }
}
