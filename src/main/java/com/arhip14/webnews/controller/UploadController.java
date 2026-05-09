package com.arhip14.webnews.controller;

import com.arhip14.webnews.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return uploadToCloudinary(file);
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        return uploadToCloudinary(file);
    }

    private ResponseEntity<?> uploadToCloudinary(MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("Файл порожній");

        try {
            String url = cloudinaryService.uploadFile(file);
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Помилка збереження: " + e.getMessage());
        }
    }
}