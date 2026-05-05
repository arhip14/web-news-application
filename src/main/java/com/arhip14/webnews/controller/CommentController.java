package com.arhip14.webnews.controller;

import com.arhip14.webnews.dto.CommentDTO;
import com.arhip14.webnews.entity.Comment;
import com.arhip14.webnews.entity.News;
import com.arhip14.webnews.entity.User;
import com.arhip14.webnews.repository.CommentRepository;
import com.arhip14.webnews.repository.NewsRepository;
import com.arhip14.webnews.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/news/{newsId}")
    public List<CommentDTO> getCommentsByNews(@PathVariable Long newsId) {
        // Отримуємо всі коментарі (від найстаріших до новіших, щоб читати як чат)
        return commentRepository.findByNewsIdOrderByCreatedAtDesc(newsId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentDTO dto, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        News news = newsRepository.findById(dto.getNewsId()).orElseThrow();

        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setNews(news);
        comment.setAuthor(user);

        // Якщо користувач відповів на інший коментар
        if (dto.getParentId() != null) {
            Comment parent = commentRepository.findById(dto.getParentId()).orElse(null);
            comment.setParentComment(parent);
        }

        commentRepository.save(comment);
        return ResponseEntity.ok(convertToDTO(comment));
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setNewsId(comment.getNews().getId());
        dto.setAuthorName(comment.getAuthor().getFullName());

        // Передаємо ID батька на фронтенд
        if (comment.getParentComment() != null) {
            dto.setParentId(comment.getParentComment().getId());
        }
        return dto;
    }
}