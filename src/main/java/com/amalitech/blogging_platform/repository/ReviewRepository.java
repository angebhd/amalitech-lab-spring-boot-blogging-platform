package com.amalitech.blogging_platform.repository;

import com.amalitech.blogging_platform.model.Post;
import com.amalitech.blogging_platform.model.Review;
import com.amalitech.blogging_platform.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
  Page<Review> findByPost_Id(Long id, Pageable pageable);
  Page<Review> findByUser_Id(Long id, Pageable pageable);
  void deleteByUser(User user); void deleteByPost(Post post);
  List<Review> findByUser(User user); long countByUser(User user);
}
