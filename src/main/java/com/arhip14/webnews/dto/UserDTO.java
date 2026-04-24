package com.arhip14.webnews.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String email;
    private String password;
    private String fullName;
    private String bio;
    private String avatarUrl;
    private String role;
}
