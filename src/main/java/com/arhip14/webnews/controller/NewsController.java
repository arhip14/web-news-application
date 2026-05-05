package com.arhip14.webnews.controller;

import com.arhip14.webnews.dto.NewsDTO;
import com.arhip14.webnews.repository.NewsRepository;
import com.arhip14.webnews.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;
    private final NewsRepository newsRepository;

    public NewsController(NewsService newsService,
                          NewsRepository newsRepository) {
        this.newsService = newsService;
        this.newsRepository = newsRepository;
    }

    @GetMapping
    public List<NewsDTO> getAllNews() {
        return newsService.getAllNews();
    }

    @PreAuthorize("hasAnyRole('CREATOR','ADMIN')")
    @PostMapping
    public NewsDTO createNews(@RequestBody NewsDTO dto) {
        return newsService.createNews(dto);
    }

    @DeleteMapping("/{id}")
// Дозволяємо видаляти або Адміну, або автору новини (якщо додасте перевірку)
    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    public ResponseEntity<?> deleteNews(@PathVariable Long id) {
        newsRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}