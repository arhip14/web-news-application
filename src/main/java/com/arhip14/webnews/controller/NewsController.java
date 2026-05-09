package com.arhip14.webnews.controller;

import com.arhip14.webnews.dto.NewsDTO;
import com.arhip14.webnews.repository.NewsRepository;
import com.arhip14.webnews.service.NewsService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;
    private final NewsRepository newsRepository;

    public NewsController(NewsService newsService, NewsRepository newsRepository) {
        this.newsService = newsService;
        this.newsRepository = newsRepository;
    }

    @GetMapping
    public Page<NewsDTO> getAllNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        return newsService.getAllNews(page, size);
    }

    @PreAuthorize("hasAnyRole('CREATOR','ADMIN')")
    @PostMapping
    public NewsDTO createNews(@Valid @RequestBody NewsDTO dto) {
        return newsService.createNews(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    public ResponseEntity<?> deleteNews(@PathVariable Long id, org.springframework.security.core.Authentication authentication) {
        com.arhip14.webnews.entity.News news = newsRepository.findById(id).orElseThrow();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !news.getAuthor().getEmail().equals(authentication.getName())) {
            return ResponseEntity.status(403).body("Помилка: Ви можете видаляти лише свої публікації!");
        }

        newsRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}