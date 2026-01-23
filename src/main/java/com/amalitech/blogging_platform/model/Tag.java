package com.amalitech.blogging_platform.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.util.List;

@Entity
@Table(name = "tags")
@SQLDelete(sql = "UPDATE tags SET is_deleted = true, deleted_at = NOW() WHERE id=?")
@Getter
@Setter
public class Tag extends BaseEntity{

  @Column(nullable = false, unique = true, length = 20)
  private String name;

  @ManyToMany(mappedBy = "tags",  fetch = FetchType.LAZY)
  private List<Post> posts;

}
