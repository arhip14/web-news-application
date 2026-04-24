package com.arhip14.webnews.controller;

import com.arhip14.webnews.dto.UserDTO;
import com.arhip14.webnews.entity.User;
import com.arhip14.webnews.repository.UserRepository;
import com.arhip14.webnews.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        // Вручну або через мапер перетворюємо DTO в Entity
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setFullName(userDTO.getFullName());
        user.setBio(userDTO.getBio());
        user.setAvatarUrl(userDTO.getAvatarUrl());
        
        // Перетворюємо рядок ролі в Enum
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
}
