package com.arhip14.webnews.service.impl;

import com.arhip14.webnews.dto.NewsDTO;
import com.arhip14.webnews.entity.News;
import com.arhip14.webnews.entity.User;
import com.arhip14.webnews.mapper.NewsMapper;
import com.arhip14.webnews.repository.NewsRepository;
import com.arhip14.webnews.repository.CategoryRepository;
import com.arhip14.webnews.repository.UserRepository;
import com.arhip14.webnews.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NewsMapper newsMapper;

    @Override
    public List<NewsDTO> getAllNews() {
        return newsRepository.findAll().stream()
                .map(newsMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NewsDTO createNews(NewsDTO newsDTO) {
        News news = newsMapper.toEntity(newsDTO);

        // 1. Прив'язка категорії
        if (newsDTO.getCategoryId() != null) {
            categoryRepository.findById(newsDTO.getCategoryId())
                    .ifPresent(news::setCategory);
        }

        // 2. АВТОМАТИЧНА ПРИВ'ЯЗКА АВТОРА (Беремо з контексту безпеки)
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByEmail(currentEmail).ifPresent(news::setAuthor);

        News savedNews = newsRepository.save(news);
        return newsMapper.toDTO(savedNews);
    }
}