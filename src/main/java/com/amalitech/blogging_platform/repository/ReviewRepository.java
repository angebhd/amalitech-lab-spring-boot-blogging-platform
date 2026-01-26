package com.amalitech.blogging_platform.repository;

import com.amalitech.blogging_platform.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
