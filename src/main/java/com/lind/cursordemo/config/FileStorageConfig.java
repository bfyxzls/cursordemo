package com.lind.cursordemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.io.File;

@Configuration
public class FileStorageConfig {
    
    @Value("${file.upload-dir:${user.home}/uploads}")
    private String uploadDir;
    
    @PostConstruct
    public void init() {
        try {
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }
    
    public String getUploadDir() {
        return uploadDir;
    }
}