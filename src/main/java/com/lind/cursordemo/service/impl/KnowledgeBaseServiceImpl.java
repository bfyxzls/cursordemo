package com.lind.cursordemo.service.impl;

import com.lind.cursordemo.config.FileStorageConfig;
import com.lind.cursordemo.entity.KnowledgeBase;
import com.lind.cursordemo.mapper.KnowledgeBaseMapper;
import com.lind.cursordemo.service.KnowledgeBaseService;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final FileStorageConfig fileStorageConfig;

    public KnowledgeBaseServiceImpl(KnowledgeBaseMapper knowledgeBaseMapper, FileStorageConfig fileStorageConfig) {
        this.knowledgeBaseMapper = knowledgeBaseMapper;
        this.fileStorageConfig = fileStorageConfig;
    }

    @Override
    public List<KnowledgeBase> findAll() {
        return knowledgeBaseMapper.findAll();
    }

    @Override
    public KnowledgeBase findById(Long id) {
        return knowledgeBaseMapper.findById(id);
    }

    @Override
    public KnowledgeBase create(KnowledgeBase knowledgeBase, MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            File uploadDir = new File(fileStorageConfig.getUploadDir());
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            File destFile = new File(uploadDir, fileName);
            file.transferTo(destFile);
            
            knowledgeBase.setFileName(file.getOriginalFilename());
            knowledgeBase.setFilePath(destFile.getAbsolutePath());
            knowledgeBase.setFileType(file.getContentType());
            knowledgeBase.setFileSize(file.getSize());
            knowledgeBase.setCreateTime(LocalDateTime.now());
            knowledgeBase.setUpdateTime(LocalDateTime.now());
            
            knowledgeBaseMapper.insert(knowledgeBase);
            return knowledgeBase;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public KnowledgeBase update(Long id, KnowledgeBase knowledgeBase, MultipartFile file) {
        KnowledgeBase existing = findById(id);
        if (existing == null) {
            throw new RuntimeException("Knowledge base not found");
        }
        
        if (file != null && !file.isEmpty()) {
            try {
                // Delete old file
                File oldFile = new File(existing.getFilePath());
                if (oldFile.exists()) {
                    oldFile.delete();
                }
                
                // Save new file
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                File destFile = new File(fileStorageConfig.getUploadDir(), fileName);
                file.transferTo(destFile);
                
                knowledgeBase.setFileName(file.getOriginalFilename());
                knowledgeBase.setFilePath(destFile.getAbsolutePath());
                knowledgeBase.setFileType(file.getContentType());
                knowledgeBase.setFileSize(file.getSize());
            } catch (IOException e) {
                throw new RuntimeException("Failed to update file", e);
            }
        } else {
            knowledgeBase.setFileName(existing.getFileName());
            knowledgeBase.setFilePath(existing.getFilePath());
            knowledgeBase.setFileType(existing.getFileType());
            knowledgeBase.setFileSize(existing.getFileSize());
        }
        
        knowledgeBase.setId(id);
        knowledgeBase.setUpdateTime(LocalDateTime.now());
        knowledgeBaseMapper.update(knowledgeBase);
        return knowledgeBase;
    }

    @Override
    public void delete(Long id) {
        KnowledgeBase knowledgeBase = findById(id);
        if (knowledgeBase != null) {
            File file = new File(knowledgeBase.getFilePath());
            if (file.exists()) {
                file.delete();
            }
            knowledgeBaseMapper.deleteById(id);
        }
    }

    @Override
    public byte[] downloadFile(Long id) {
        try {
            KnowledgeBase knowledgeBase = findById(id);
            if (knowledgeBase == null) {
                throw new RuntimeException("File not found");
            }
            return FileUtils.readFileToByteArray(new File(knowledgeBase.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to download file", e);
        }
    }
}