package com.amalitech.blogging_platform.service;



import com.amalitech.blogging_platform.dao.CommentDAO;
import com.amalitech.blogging_platform.dto.CommentDTO;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.Comment;
import com.amalitech.blogging_platform.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
  private final CommentDAO commentDAO;
  private final CommentRepository commentRepository;

  @Autowired
  public CommentService(CommentRepository commentRepository, CommentDAO commentDAO){
    this.commentRepository = commentRepository;
    this.commentDAO = commentDAO;
  }

  /**
   * Retrieves paginated comments.
   *
   * @param pageable object containing page number size, and sort method
   * @return paginated list of CommentDTO.Out
   */
  public Page<CommentDTO.Out> get(Pageable pageable){
    return this.commentRepository.findAll(pageable).map(this::mapToDTO);
  }

  /**
   * Retrieves a single comment by ID.
   *
   * @param id comment ID
   * @return CommentDTO.Out representing the comment
   * @throws RessourceNotFoundException if the comment does not exist
   */
  public CommentDTO.Out get(Long id){
    return this.commentRepository.findById(id).map(this::mapToDTO).orElseThrow(() -> new RessourceNotFoundException("Comment not found"));
  }

  /**
   * Retrieves all comments for a given post.
   *
   * @param postId ID of the post
   * @return list of CommentDTO.Out for the post
   */
  public Page<CommentDTO.Out> getByPostId(Long postId){
    return commentRepository.findByPost_Id(postId, Pageable.unpaged()).map(this::mapToDTO);
  }

  /**
   * Retrieves all comments made by a user.
   *
   * @param userId ID of the user
   * @return list of Comment entities
   */
  public Page<CommentDTO.Out> getByUserId(Long userId){
    return commentRepository.findByUser_Id(userId, Pageable.unpaged()).map(this::mapToDTO);
  }


  /**
   * Creates a new comment.
   *
   * @param in input DTO containing comment data
   * @return CommentDTO.Out representing the created comment
   */
  public CommentDTO.Out create(CommentDTO.In in){
    return  this.mapToDTO(this.commentRepository.save(this.mapToEntity(in)));
  }

  /**
   * Updates the body of an existing comment.
   *
   * @param id   ID of the comment to update
   * @param body new comment body
   * @return CommentDTO.Out representing the updated comment
   * @throws RessourceNotFoundException if the comment does not exist
   */
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
  public void delete (Long id){
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
    dto.setPostId(entity.getPostId());
    dto.setUserId(entity.getUserId());
    dto.setBody(entity.getBody());
    dto.setParentCommentId(entity.getParentCommentId());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setUpdatedAt(entity.getUpdatedAt());
    dto.setDeletedAt(entity.getDeletedAt());
    dto.setDeleted(entity.isDeleted());
    return dto;
  }

  /**
   * Converts a CommentDTO.In to a Comment entity.
   *
   * @param in CommentDTO.In input
   * @return Comment entity
   */
  private Comment mapToEntity(CommentDTO.In in){
    Comment entity = new Comment();
    entity.setPostId(in.getPostId());
    entity.setUserId(in.getUserId());
    entity.setBody(in.getBody());
    entity.setParentCommentId(in.getParentCommentId());

    return entity;
  }

}
