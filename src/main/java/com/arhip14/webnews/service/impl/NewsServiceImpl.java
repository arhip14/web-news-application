package com.arhip14.webnews.service.impl;

import com.arhip14.webnews.dto.NewsDTO;
import com.arhip14.webnews.entity.News;
import com.arhip14.webnews.mapper.NewsMapper;
import com.arhip14.webnews.repository.CategoryRepository;
import com.arhip14.webnews.repository.NewsRepository;
import com.arhip14.webnews.repository.UserRepository;
import com.arhip14.webnews.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
    public Page<NewsDTO> getAllNews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return newsRepository.findAll(pageable).map(newsMapper::toDTO);
    }

    @Override
    public NewsDTO createNews(NewsDTO newsDTO) {
        News news = newsMapper.toEntity(newsDTO);

        if (newsDTO.getCategoryId() != null) {
            categoryRepository.findById(newsDTO.getCategoryId())
                    .ifPresent(news::setCategory);
        }

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByEmail(currentEmail).ifPresent(news::setAuthor);

        News savedNews = newsRepository.save(news);
        return newsMapper.toDTO(savedNews);
    }
}