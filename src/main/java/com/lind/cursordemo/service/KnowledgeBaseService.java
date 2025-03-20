package com.lind.cursordemo.service;

import com.lind.cursordemo.entity.KnowledgeBase;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface KnowledgeBaseService {
    List<KnowledgeBase> findAll();
    
    KnowledgeBase findById(Long id);
    
    KnowledgeBase create(KnowledgeBase knowledgeBase, MultipartFile file);
    
    KnowledgeBase update(Long id, KnowledgeBase knowledgeBase, MultipartFile file);
    
    void delete(Long id);
    
    byte[] downloadFile(Long id);
}