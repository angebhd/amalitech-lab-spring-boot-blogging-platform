package com.amalitech.blogging_platform.dto;

import com.amalitech.blogging_platform.model.Comment;
import com.amalitech.blogging_platform.model.Post;
import com.amalitech.blogging_platform.model.Review;
import com.amalitech.blogging_platform.model.Tag;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostDTO {

  private Post post;
  private Long authorId;
  private String authorName;
  private List<CommentDTO> commentDTOS;
  private List<Comment> comments;
  private List<Tag> tags;
  private List<Review> reviews;

}
