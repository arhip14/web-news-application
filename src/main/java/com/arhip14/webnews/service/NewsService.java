package com.arhip14.webnews.service;

import com.arhip14.webnews.dto.NewsDTO;
import org.springframework.data.domain.Page;

public interface NewsService {
    Page<NewsDTO> getAllNews(int page, int size);
    NewsDTO createNews(NewsDTO newsDTO);
}