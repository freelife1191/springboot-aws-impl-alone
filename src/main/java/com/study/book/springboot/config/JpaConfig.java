package com.study.book.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @EnableJpaAuditing 사용하기 위해선 최소 하나의 @Entity 클래스가 필요
 * @WebMvcTest 에서는 스캔하지 않도록 클래스를 분리
 */
@Configuration
@EnableJpaAuditing // JPA Auditing 활성화
public class JpaConfig {}
