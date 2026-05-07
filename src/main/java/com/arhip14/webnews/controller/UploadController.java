package com.arhip14.webnews.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return saveFile(file, "uploads/avatars/");
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        return saveFile(file, "uploads/news/");
    }

    private ResponseEntity<?> saveFile(MultipartFile file, String subDir) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("Файл порожній");

        try {
            File dir = new File(subDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_");
            Path filePath = Paths.get(subDir + fileName);
            Files.write(filePath, file.getBytes());

            return ResponseEntity.ok("/" + subDir + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Помилка збереження: " + e.getMessage());
        }
    }
}