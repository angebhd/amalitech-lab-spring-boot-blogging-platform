package com.amalitech.blogging_platform.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@SQLDelete(sql = "UPDATE posts SET is_deleted = true, deleted_at = NOW() WHERE id=?")
@Getter
@Setter
public class Post extends  BaseEntity {


  @ManyToOne(fetch = FetchType.EAGER,  optional = false)
  @JoinColumn(name = "author_id")
  private User author;

  @OneToMany(mappedBy = "post",  fetch = FetchType.LAZY)
  private List<Review> reviews;

  @OneToMany(mappedBy = "post",  fetch = FetchType.LAZY)
  private List<Comment> comments;

  @Column(nullable = false, length = 100)
  private String title;

  @Lob()
  private String body;


  @ManyToMany()
  @JoinTable(
          name = "post_tags"
          , joinColumns = @JoinColumn(name = "post_id"),
          inverseJoinColumns = @JoinColumn(name = "tag_id")
  )
  private List<Tag> tags = new ArrayList<>();

}
