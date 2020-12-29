package com.study.book.springboot.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by KMS on 2020/12/29.
 */
public interface PostsRepository extends JpaRepository<Posts, Long> {
}
