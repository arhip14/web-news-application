package com.arhip14.webnews.service;

import com.arhip14.webnews.entity.User;
import com.arhip14.webnews.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Помилка: Користувач з таким Email вже існує!";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Перевірка: якщо роль не прийшла з фронтенду, ставимо READER
        if (user.getRole() == null) {
            user.setRole(User.Role.READER);
        }

        userRepository.save(user);
        return "Користувач успішно зареєстрований як " + user.getRole();
    }
}
