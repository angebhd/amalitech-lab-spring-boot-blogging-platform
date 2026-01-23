package com.amalitech.blogging_platform.repository;

import com.amalitech.blogging_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
