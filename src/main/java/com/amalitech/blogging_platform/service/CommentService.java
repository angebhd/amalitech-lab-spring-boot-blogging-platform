package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dto.CommentDTO;
import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.exceptions.DataConflictException;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.BaseEntity;
import com.amalitech.blogging_platform.model.Comment;
import com.amalitech.blogging_platform.repository.CommentRepository;
import com.amalitech.blogging_platform.repository.PostRepository;
import com.amalitech.blogging_platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * Service layer for managing comments.
 * <p>
 * Provides CRUD operations and query methods to retrieve comments by post or user.
 * Converts between Comment entities and CommentDTOs.
 */
@Service
public class CommentService {
  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;

  @Autowired
  public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository){
    this.commentRepository = commentRepository;
    this.postRepository = postRepository;
    this.userRepository = userRepository;
  }

  /**
   * Retrieves paginated comments.
   *
   * @param pageable object containing page number size, and sort method
   * @return paginated list of CommentDTO.Out
   */
  public PaginatedData<CommentDTO.Out> get(Pageable pageable){
    var data = this.commentRepository.findAll(pageable).map(this::mapToDTO);
    return new PaginatedData<>(data);
  }

  /**
   * Retrieves a single comment by ID.
   *
   * @param id comment ID
   * @return CommentDTO.Out representing the comment
   * @throws RessourceNotFoundException if the comment does not exist
   */
  @Cacheable(cacheNames = "comments", key = "#id")
  public CommentDTO.Out get(Long id){
    return this.commentRepository.findById(id).map(this::mapToDTO).orElseThrow(() -> new RessourceNotFoundException("Comment not found"));
  }

  /**
   * Retrieves all comments for a given post.
   *
   * @param postId ID of the post
   * @return list of CommentDTO.Out for the post
   */
  public PaginatedData<CommentDTO.Out> getByPostId(Long postId){
    return new PaginatedData<>(commentRepository.findByPost_Id(postId, Pageable.unpaged()).map(this::mapToDTO));
  }

  /**
   * Retrieves all comments made by a user.
   *
   * @param userId ID of the user
   * @return list of Comment entities
   */
  public PaginatedData<CommentDTO.Out> getByUserId(Long userId){
    return new PaginatedData<>(commentRepository.findByUser_Id(userId, Pageable.unpaged()).map(this::mapToDTO));
  }


  /**
   * Creates a new comment.
   *
   * @param in input DTO containing comment data
   * @return CommentDTO.Out representing the created comment
   */
  @Transactional
  @CachePut(cacheNames = "comments", key = "#result.id")
  public CommentDTO.Out create(CommentDTO.In in){
    Comment comment = new Comment();
    comment.setBody(in.getBody());

    var post = this.postRepository.findById(in.getPostId()).orElseThrow(() -> new DataConflictException("Post not found, with id " + in.getPostId()));
    var user = this.userRepository.findById(in.getUserId()).orElseThrow(() -> new DataConflictException("User not found, with id " + in.getUserId()));
    if (in.getParentCommentId() != null) {
      var parent = this.commentRepository.findById(in.getParentCommentId()).orElseThrow(() -> new DataConflictException("Parent comment not found, with id " + in.getParentCommentId()));
      if(!parent.getPost().getId().equals(post.getId())){
        throw new DataConflictException("Comment and parent comment should have the same post ID");
      }
      comment.setParentComment(parent);
    }
    comment.setPost(post);
    comment.setUser(user);
    return  this.mapToDTO(this.commentRepository.save(comment));
  }

  /**
   * Updates the body of an existing comment.
   *
   * @param id   ID of the comment to update
   * @param body new comment body
   * @return CommentDTO.Out representing the updated comment
   * @throws RessourceNotFoundException if the comment does not exist
   */
  @CachePut(cacheNames = "comments", key = "#id")
  public CommentDTO.Out update (Long id, String body){
    Comment old = this.commentRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("Comment not found"));
    old.setBody(body);
    return this.mapToDTO(this.commentRepository.save(old));
  }

  /**
   * Deletes a comment by ID.
   *
   * @param id ID of the comment to delete
   */
  @Transactional
  @CacheEvict(cacheNames = "comments", key = "#id")
  public void delete (Long id){
    Comment comment = this.commentRepository.findById(id).orElseThrow(() -> new DataConflictException("Comment not found"));
    if(!comment.getChildren().isEmpty()){
      commentRepository.deleteAllByIdInBatch(comment.getChildren().stream().map(BaseEntity::getId).toList());
    }
    this.commentRepository.deleteById(id);
  }

  /**
   * Converts a Comment entity to a CommentDTO.Out.
   *
   * @param entity Comment entity
   * @return CommentDTO.Out
   */
  private CommentDTO.Out mapToDTO(Comment entity){
    CommentDTO.Out dto = new CommentDTO.Out();
    dto.setId(entity.getId());
    dto.setPostId(entity.getPost().getId());
    dto.setUser(UserDTO.Converter.toDTO(entity.getUser())); // call the Converter, and move it to DTO Converter class
    dto.setBody(entity.getBody());
    if(entity.getParentComment() != null)
     dto.setParentCommentId(entity.getParentComment().getId());

    dto.setCreatedAt(entity.getCreatedAt());
    dto.setUpdatedAt(entity.getUpdatedAt());
    dto.setDeletedAt(entity.getDeletedAt());
    dto.setDeleted(entity.isDeleted());
    return dto;
  }
}
