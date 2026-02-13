package com.amalitech.blogging_platform.service;


import com.amalitech.blogging_platform.repository.CommentRepository;
import com.amalitech.blogging_platform.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ModerationService {
  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final Logger logger = LoggerFactory.getLogger(ModerationService.class);
  private static final String INVALID_COMMENT = "This comment violates our policies";
  public ModerationService(CommentRepository commentRepository, PostRepository postRepository) {
    this.commentRepository = commentRepository;
    this.postRepository = postRepository;
  }

  private boolean verifyText(String text) {
    return text.toLowerCase().contains("javac");
  }

  @Async
  @CacheEvict(cacheNames = "comments", key = "#comment.id")
  public void validateComment(long commentId) {
    var comment = this.commentRepository.findById(commentId).orElse(null);
    if (comment == null) return;
    try {

      var isCommentValid = this.verifyText(comment.getBody());
      Thread.sleep(500);
      if (!isCommentValid) {
        this.logger.warn("Comments `{}` violates the policy and its going to be hidden", comment.getBody());

        comment.setBody(INVALID_COMMENT);
        this.commentRepository.save(comment);
      }
    } catch (InterruptedException e) {
      this.logger.error("Comment validation failed [ {} ]", e.getMessage());
      Thread.currentThread().interrupt();
    }
  }

  @Async
  @CacheEvict(cacheNames = "posts", key = "#post.id")
  public void validatePost(long postId) {
    var post = this.postRepository.findById(postId).orElse(null);
    if (post == null) return;
    try {
      var isCommentValid = this.verifyText(post.getBody());
      Thread.sleep(500);
      if (!isCommentValid) {
        this.logger.warn("Post `{}` violates the policy and its going to be hidden", post.getBody());
        post.setBody(INVALID_COMMENT);
        this.postRepository.save(post);
      }
    } catch (InterruptedException e) {
      this.logger.error("Post validation failed [ {} ]", e.getMessage());
      Thread.currentThread().interrupt();
    }
  }
}
