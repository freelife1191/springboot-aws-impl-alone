package com.study.book.springboot.web.dto;

import com.study.book.springboot.domain.posts.Posts;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by KMS on 2020/12/30.
 */
@Getter
@ToString
public class PostsResponseDto {
    
    private Long id;
    private String title;
    private String content;
    private String author;
    
    public PostsResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
    }
}
