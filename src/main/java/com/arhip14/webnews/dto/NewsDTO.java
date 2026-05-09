package com.arhip14.webnews.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NewsDTO {
    private Long id;

    @NotBlank(message = "Заголовок не може бути порожнім")
    @Size(max = 200, message = "Заголовок занадто довгий")
    private String title;

    @NotBlank(message = "Текст новини обов'язковий")
    private String content;

    private LocalDateTime createdAt;
    private Long categoryId;
    private String categoryName;
    private String authorEmail;
    private String imageUrl;

}
