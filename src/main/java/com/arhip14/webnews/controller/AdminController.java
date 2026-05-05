package com.arhip14.webnews.controller;

import com.arhip14.webnews.entity.User;
import com.arhip14.webnews.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Доступ тільки для ROLE_ADMIN
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    // Отримати всіх користувачів для таблиці
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Змінити роль (READER -> CREATOR -> ADMIN)
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> changeUserRole(@PathVariable Long id, @RequestParam String role) {
        User user = userRepository.findById(id).orElseThrow();

        // Перетворюємо рядок на Enum
        try {
            user.setRole(User.Role.valueOf(role.toUpperCase()));
            userRepository.save(user);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Невірна роль");
        }
    }

    // Видалити користувача (якщо потрібно за ТЗ)
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}