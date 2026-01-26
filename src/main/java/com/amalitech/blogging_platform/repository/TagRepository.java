package com.amalitech.blogging_platform.repository;

import com.amalitech.blogging_platform.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
  Optional<Tag> findByNameIgnoreCase(String name); boolean existsByNameIgnoreCase(String name);
  List<Tag> findAllByNameInIgnoreCase(List<String> names);
}
