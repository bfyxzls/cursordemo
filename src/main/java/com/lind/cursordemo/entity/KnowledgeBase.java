package com.lind.cursordemo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class KnowledgeBase {
    private Long id;
    private String title;
    private String description;
    private String filePath;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 