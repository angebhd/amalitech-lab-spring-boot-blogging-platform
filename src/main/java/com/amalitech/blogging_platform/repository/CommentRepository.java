package com.amalitech.blogging_platform.repository;

import com.amalitech.blogging_platform.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
Page<Comment> findByPost_Id(Long id, Pageable pageable); Page<Comment> findByUser_Id(Long id, Pageable pageable); }
