package com.arhip14.webnews.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {
    @NotBlank(message = "Email не може бути порожнім")
    @Email(message = "Некоректний формат email")
    private String email;

    @NotBlank(message = "Пароль обов'язковий")
    @Size(min = 6, message = "Пароль має містити мінімум 6 символів")
    private String password;

    @NotBlank(message = "Ім'я не може бути порожнім")
    private String fullName;
    private String bio;
    private String avatarUrl;
    private String role;
}
