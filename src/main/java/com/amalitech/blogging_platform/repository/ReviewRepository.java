package com.amalitech.blogging_platform.repository;

import com.amalitech.blogging_platform.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
Page<Review> findByPost_Id(Long id, Pageable pageable); Page<Review> findByUser_Id(Long id, Pageable pageable); }
