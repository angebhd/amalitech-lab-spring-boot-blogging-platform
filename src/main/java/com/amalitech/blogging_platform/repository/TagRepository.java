package com.amalitech.blogging_platform.repository;

import com.amalitech.blogging_platform.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
