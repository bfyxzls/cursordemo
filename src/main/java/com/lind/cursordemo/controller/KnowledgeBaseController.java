package com.lind.cursordemo.controller;

import com.lind.cursordemo.entity.KnowledgeBase;
import com.lind.cursordemo.service.KnowledgeBaseService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@RestController
@RequestMapping("/api/knowledge-base")
@CrossOrigin
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;
    private final ObjectMapper objectMapper;

    public KnowledgeBaseController(KnowledgeBaseService knowledgeBaseService, ObjectMapper objectMapper) {
        this.knowledgeBaseService = knowledgeBaseService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public List<KnowledgeBase> findAll() {
        return knowledgeBaseService.findAll();
    }

    @GetMapping("/{id}")
    public KnowledgeBase findById(@PathVariable Long id) {
        return knowledgeBaseService.findById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public KnowledgeBase create(
            @RequestParam("file") MultipartFile file,
            @RequestParam("knowledgeBase") String knowledgeBaseJson) {
        try {
            KnowledgeBase knowledgeBase = objectMapper.readValue(knowledgeBaseJson, KnowledgeBase.class);
            return knowledgeBaseService.create(knowledgeBase, file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process upload: " + e.getMessage(), e);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public KnowledgeBase update(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("knowledgeBase") String knowledgeBaseJson) {
        try {
            KnowledgeBase knowledgeBase = objectMapper.readValue(knowledgeBaseJson, KnowledgeBase.class);
            return knowledgeBaseService.update(id, knowledgeBase, file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process update: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        knowledgeBaseService.delete(id);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        KnowledgeBase knowledgeBase = knowledgeBaseService.findById(id);
        byte[] fileContent = knowledgeBaseService.downloadFile(id);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(knowledgeBase.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + knowledgeBase.getFileName() + "\"")
                .body(fileContent);
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<?> previewFile(@PathVariable Long id) {
        KnowledgeBase knowledgeBase = knowledgeBaseService.findById(id);
        if (knowledgeBase == null) {
            return ResponseEntity.notFound().build();
        }

        String fileType = knowledgeBase.getFileType();
        byte[] content = knowledgeBaseService.downloadFile(id);

        // 处理文本文件
        if (fileType.contains("text/plain")) {
            String textContent = new String(content, StandardCharsets.UTF_8);
            Map<String, Object> response = new HashMap<>();
            response.put("type", "text");
            response.put("content", textContent);
            return ResponseEntity.ok(response);
        }

        // 处理图片文件
        if (fileType.startsWith("image/")) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileType))
                    .body(content);
        }

        // 处理PDF文件
        if (fileType.equals("application/pdf")) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(content);
        }

        // 处理HTML文件
        if (fileType.contains("text/html")) {
            String htmlContent = new String(content, StandardCharsets.UTF_8);
            Map<String, Object> response = new HashMap<>();
            response.put("type", "html");
            response.put("content", htmlContent);
            return ResponseEntity.ok(response);
        }

        // 其他类型文件
        Map<String, Object> response = new HashMap<>();
        response.put("type", "unsupported");
        response.put("message", "不支持预览该类型的文件");
        return ResponseEntity.ok(response);
    }
} 