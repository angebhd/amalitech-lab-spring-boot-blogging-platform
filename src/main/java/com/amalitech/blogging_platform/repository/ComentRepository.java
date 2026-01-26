package com.amalitech.blogging_platform.repository;

import com.amalitech.blogging_platform.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComentRepository extends JpaRepository<Comment, Long> {
}
