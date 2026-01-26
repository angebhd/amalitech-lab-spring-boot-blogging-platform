package com.amalitech.blogging_platform.repository;

import com.amalitech.blogging_platform.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
Page<Post> findByAuthor_Id(Long id, Pageable pageable); }
