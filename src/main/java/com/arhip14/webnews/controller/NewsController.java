package com.arhip14.webnews.controller;

import com.arhip14.webnews.dto.NewsDTO;
import com.arhip14.webnews.service.NewsService;
import com.arhip14.webnews.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;
    
    @Autowired
    private NewsRepository newsRepository;

    @GetMapping
    public List<NewsDTO> getAllNews() {
        return newsService.getAllNews();
    }

    @PostMapping
    public NewsDTO createNews(@RequestBody NewsDTO newsDTO) {
        return newsService.createNews(newsDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        if (newsRepository.existsById(id)) {
            newsRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
