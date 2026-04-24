package com.arhip14.webnews.mapper;

import com.arhip14.webnews.dto.NewsDTO;
import com.arhip14.webnews.entity.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "authorEmail", source = "author.email")
    NewsDTO toDTO(News news);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "author", ignore = true)
    News toEntity(NewsDTO newsDTO);
}
