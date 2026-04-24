package com.arhip14.webnews.mapper;

import com.arhip14.webnews.dto.NewsDTO;
import com.arhip14.webnews.entity.Category;
import com.arhip14.webnews.entity.News;
import com.arhip14.webnews.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-24T20:48:18+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class NewsMapperImpl implements NewsMapper {

    @Override
    public NewsDTO toDTO(News news) {
        if ( news == null ) {
            return null;
        }

        NewsDTO newsDTO = new NewsDTO();

        newsDTO.setCategoryName( newsCategoryName( news ) );
        newsDTO.setCategoryId( newsCategoryId( news ) );
        newsDTO.setAuthorEmail( newsAuthorEmail( news ) );
        newsDTO.setId( news.getId() );
        newsDTO.setTitle( news.getTitle() );
        newsDTO.setContent( news.getContent() );
        newsDTO.setCreatedAt( news.getCreatedAt() );

        return newsDTO;
    }

    @Override
    public News toEntity(NewsDTO newsDTO) {
        if ( newsDTO == null ) {
            return null;
        }

        News news = new News();

        news.setTitle( newsDTO.getTitle() );
        news.setContent( newsDTO.getContent() );

        return news;
    }

    private String newsCategoryName(News news) {
        if ( news == null ) {
            return null;
        }
        Category category = news.getCategory();
        if ( category == null ) {
            return null;
        }
        String name = category.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long newsCategoryId(News news) {
        if ( news == null ) {
            return null;
        }
        Category category = news.getCategory();
        if ( category == null ) {
            return null;
        }
        Long id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String newsAuthorEmail(News news) {
        if ( news == null ) {
            return null;
        }
        User author = news.getAuthor();
        if ( author == null ) {
            return null;
        }
        String email = author.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }
}
