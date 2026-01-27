package com.amalitech.blogging_platform.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "reviews")
@SQLDelete(sql = "UPDATE reviews SET is_deleted = true, deleted_at = NOW() WHERE id=?")
@Getter
@Setter
public class Review extends BaseEntity {

  @Transient
  private Long postId;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Transient
  private Long userId;

  @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.REMOVE)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, length = 5)
  @Enumerated(EnumType.STRING)
  private EReview rate;


  public String getRate() {
    return this.rate.name();
  }
  public void setRate(String name){
    this.rate = EReview.valueOf(name);
  }
}
