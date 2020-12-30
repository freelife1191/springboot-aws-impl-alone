package com.study.book.springboot.web;

import com.study.book.springboot.service.posts.PostsService;
import com.study.book.springboot.web.dto.PostsResponseDto;
import com.study.book.springboot.web.dto.PostsSaveRequestDto;
import com.study.book.springboot.web.dto.PostsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Created by KMS on 2020/12/30.
 */
@RequiredArgsConstructor
@RestController
public class PostsApiController {
    
    private final PostsService postsService;
    
    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDto requestDto) {
        return postsService.save(requestDto);
    }
    
    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) {
        return postsService.update(id, requestDto);
    }
    
    @DeleteMapping("/api/v1/posts/{id}")
    public Long delete(@PathVariable Long id) {
        postsService.delete(id);
        return id;
    }
    
    @GetMapping("/api/v1/posts/{id}")
    public PostsResponseDto findById(@PathVariable Long id) {
        return postsService.findById(id);
    }
}