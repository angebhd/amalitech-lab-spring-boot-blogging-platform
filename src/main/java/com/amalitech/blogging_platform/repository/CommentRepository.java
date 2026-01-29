package com.amalitech.blogging_platform.repository;

import com.amalitech.blogging_platform.model.Comment;
import com.amalitech.blogging_platform.model.Post;
import com.amalitech.blogging_platform.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  @EntityGraph(attributePaths = "user")
  Page<Comment> findAll(Pageable pageable);
  Page<Comment> findByPost_Id(Long id, Pageable pageable);
  Page<Comment> findByUser_Id(Long id, Pageable pageable);
void deleteByUser(User user); void deleteByPost(Post post); long deleteByChildren(Comment children); }
