package com.study.book.springboot.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by KMS on 2020/12/29.
 */
@Getter
@RequiredArgsConstructor
public class HelloResponseDto {
    
    private final String name;
    private final int amount;
}