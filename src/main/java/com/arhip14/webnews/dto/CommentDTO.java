package com.arhip14.webnews.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;
    private String text;
    private LocalDateTime createdAt;
    private Long newsId;
    private String authorName;
    private Long parentId;
}