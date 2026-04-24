package com.arhip14.webnews.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NewsDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Long categoryId; // Для вибору при створенні
    private String categoryName; // Для відображення на фронті
    private String authorEmail;
}
