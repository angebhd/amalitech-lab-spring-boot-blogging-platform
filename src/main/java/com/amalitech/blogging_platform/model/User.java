package com.amalitech.blogging_platform.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


import java.util.List;

@Entity
@Table(name = "users" )
@SQLDelete(sql = "UPDATE users SET is_deleted = true, deleted_at = NOW() WHERE id=?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
public class User extends BaseEntity {

  @Column(length = 30)
  private String firstName;

  @Column(length = 30)
  private String lastName;

  @Column(unique = true,  nullable = false, length = 12)
  private String username;

  @Column(unique = true,  nullable = false, length = 100)
  private String email;

  @Column()
  private String password;

  @Column(nullable = false, updatable = false, length = 5)
  @Enumerated(EnumType.STRING)
  private UserRole role = UserRole.USER;

  @OneToMany(mappedBy = "author", fetch =  FetchType.LAZY)
  private List<Post> posts;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<Review> reviews;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<Comment> comments;

}
