package com.arhip14.webnews.repository;

import com.arhip14.webnews.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByNewsIdOrderByCreatedAtDesc(Long newsId);
}