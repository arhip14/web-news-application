package com.arhip14.webnews.service;

import com.arhip14.webnews.dto.NewsDTO;
import java.util.List;

public interface NewsService {
    List<NewsDTO> getAllNews();
    NewsDTO createNews(NewsDTO newsDTO);
}
