package com.example.demo.controller;

import com.example.demo.helper.ViewHelper;
import com.example.demo.model.KeyContent;
import com.example.demo.repository.KeyContentRepository;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Controller
public class KeyContentController {

    private final KeyContentRepository repository;

    // Đường dẫn tuyệt đối lưu file
    private final Path uploadBaseDir;

    public KeyContentController(KeyContentRepository repository,
                                @Value("${upload.dir:uploads}") String uploadDir) {
        this.repository = repository;
        this.uploadBaseDir = Paths.get(System.getProperty("user.dir"), uploadDir);
        try {
            Files.createDirectories(uploadBaseDir); // Tạo thư mục nếu chưa có
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục uploads", e);
        }
    }

    @GetMapping("/content-store")
    public String index(Model model) {
        List<KeyContent> dataList = repository.findAll();
        model.addAttribute("dataList", dataList);
        ViewHelper.setView(model, "view/content-store", "Content Store");
        return "layout"; // Load layout.html
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("key") String key,
                         @RequestParam(value = "contentText", required = false) String contentText,
                         @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        KeyContent kc = new KeyContent();
        kc.setStorageKey(key);
        kc.setContentText(contentText);

        if (file != null && !file.isEmpty()) {
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String filePath = System.currentTimeMillis() + "_" + originalFilename;
            Path fullPath = uploadBaseDir.resolve(filePath);
            file.transferTo(fullPath.toFile());

            kc.setFilePath(filePath);
            kc.setFileType(file.getContentType());
        }

        repository.save(kc);
        return "redirect:/content-store";
    }

    @GetMapping("/search")
    public String search(@RequestParam("key") String key, Model model) {
        List<KeyContent> dataList = repository.findByStorageKeyContainingIgnoreCase(key);
        model.addAttribute("dataList", dataList);
        ViewHelper.setView(model, "view/content-store", "Content Store");
        return "layout"; // Load layout.html
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repository.findById(id).ifPresent(data -> {
            if (data.getFilePath() != null) {
                try {
                    Files.deleteIfExists(uploadBaseDir.resolve(data.getFilePath()));
                } catch (IOException ignored) {}
            }
            repository.deleteById(id);
        });
        return "redirect:/content-store";
    }

    // Mapping để hiển thị/tải file tĩnh từ thư mục uploads
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws IOException {
        Path filePath = uploadBaseDir.resolve(filename);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource file = new UrlResource(filePath.toUri());

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // Đảm bảo tên file tải xuống gốc được giữ lại
        String originalFilename = filename.contains("_") ? filename.substring(filename.indexOf("_") + 1) : filename;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFilename + "\"")
                .body(file);
    }


    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        KeyContent data = repository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu"));
        model.addAttribute("data", data);
        ViewHelper.setView(model, "view/content-store-edit", "Content Store");
        return "layout"; // Load layout.html
    }

    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable Long id,
                             @RequestParam("key") String key,
                             @RequestParam(value = "contentText", required = false) String contentText,
                             @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        KeyContent kc = repository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu"));
        kc.setStorageKey(key);
        kc.setContentText(contentText);

        // Nếu upload file mới
        if (file != null && !file.isEmpty()) {
            // Xoá file cũ nếu có
            if (kc.getFilePath() != null) {
                Files.deleteIfExists(uploadBaseDir.resolve(kc.getFilePath()));
            }

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String filePath = System.currentTimeMillis() + "_" + originalFilename;
            Path fullPath = uploadBaseDir.resolve(filePath);
            file.transferTo(fullPath.toFile());

            kc.setFilePath(filePath);
            kc.setFileType(file.getContentType());
        }

        repository.save(kc);
        return "redirect:/content-store";
    }

}
