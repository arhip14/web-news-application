package com.arhip14.webnews.repository;

import com.arhip14.webnews.entity.News;
import com.arhip14.webnews.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    Page<News> findByCategoryOrderByCreatedAtDesc(Category category, Pageable pageable);
    List<News> findByTitleContainingIgnoreCase(String keyword);
}
