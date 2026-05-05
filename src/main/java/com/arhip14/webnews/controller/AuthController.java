package com.arhip14.webnews.controller;

import com.arhip14.webnews.config.JwtUtils;
import com.arhip14.webnews.dto.UserDTO;
import com.arhip14.webnews.entity.User;
import com.arhip14.webnews.repository.UserRepository;
import com.arhip14.webnews.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    // ЦЬОГО МЕТОДУ НЕ ВИСТАЧАЛО У ВАШОМУ КОДІ
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDTO loginRequest) {
        try {
            // 1. Перевіряємо логін та пароль
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 2. Генеруємо токен
            String jwt = jwtUtils.generateJwtToken(loginRequest.getEmail());

            // 3. Формуємо відповідь
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("email", loginRequest.getEmail());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Якщо пароль невірний — повертаємо 401
            return ResponseEntity.status(401).body("Невірний email або пароль");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setFullName(userDTO.getFullName());
        user.setBio(userDTO.getBio());
        user.setAvatarUrl(userDTO.getAvatarUrl());

        if (userDTO.getRole() != null) {
            user.setRole(User.Role.valueOf(userDTO.getRole().toUpperCase()));
        }

        String result = authService.register(user);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        Optional<User> user = userRepository.findByEmail(authentication.getName());
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.status(404).build());
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserDTO userDTO, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();

        Optional<User> optionalUser = userRepository.findByEmail(authentication.getName());
        if (optionalUser.isEmpty()) return ResponseEntity.status(404).build();

        User user = optionalUser.get();
        if (userDTO.getFullName() != null && !userDTO.getFullName().isBlank()) {
            user.setFullName(userDTO.getFullName());
        }
        if (userDTO.getBio() != null) {
            user.setBio(userDTO.getBio());
        }
        if (userDTO.getAvatarUrl() != null) {
            user.setAvatarUrl(userDTO.getAvatarUrl());
        }

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/profile/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();

        try {
            String uploadDir = "uploads/avatars/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);

            Files.write(filePath, file.getBytes());

            User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
            user.setAvatarUrl("/uploads/avatars/" + fileName);
            userRepository.save(user);

            return ResponseEntity.ok(user);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Помилка завантаження файлу");
        }
    }
}