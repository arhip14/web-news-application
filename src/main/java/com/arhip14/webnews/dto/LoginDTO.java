package com.arhip14.webnews.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank(message = "Email обов'язковий")
    private String email;

    @NotBlank(message = "Пароль обов'язковий")
    private String password;
}