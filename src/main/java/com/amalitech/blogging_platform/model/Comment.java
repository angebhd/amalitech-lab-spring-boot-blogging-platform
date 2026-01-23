package com.amalitech.blogging_platform.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.util.List;

@Entity()
@Table(name = "comments")
@SQLDelete(sql = "UPDATE comments SET is_deleted = true, deleted_at = NOW() WHERE id=?")
@Getter
@Setter
public class Comment extends BaseEntity{


  @Transient
  private Long postId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Transient
  private Long userId;

  @ManyToOne(fetch = FetchType.LAZY,  optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Lob
  private String body;

  @Transient
  private Long parentCommentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_comment_id")
  private Comment parentComment;

  @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY)
  private List<Comment> children;

}
