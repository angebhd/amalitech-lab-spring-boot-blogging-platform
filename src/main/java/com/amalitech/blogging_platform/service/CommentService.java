package com.amalitech.blogging_platform.service;



import com.amalitech.blogging_platform.dao.CommentDAO;
import com.amalitech.blogging_platform.dao.enums.CommentColumn;
import com.amalitech.blogging_platform.dto.CommentDTO;
import com.amalitech.blogging_platform.dto.PageRequest;
import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for managing comments.
 * <p>
 * Provides CRUD operations and query methods to retrieve comments by post or user.
 * Converts between Comment entities and CommentDTOs.
 */
@Service
public class CommentService {
  private final CommentDAO commentDAO;

  @Autowired
  public CommentService(CommentDAO commentDAO){
    this.commentDAO = commentDAO;
  }

  /**
   * Retrieves paginated comments.
   *
   * @param pageRequest object containing page number and size
   * @return paginated list of CommentDTO.Out
   */
  public PaginatedData<CommentDTO.Out> get(PageRequest pageRequest){
    var res = this.commentDAO.getAll(pageRequest.getPage(), pageRequest.getSize());
    PaginatedData<CommentDTO.Out> dto = new PaginatedData<>();
    dto.setPage(res.getPage());
    dto.setPageSize(res.getPageSize());
    dto.setTotal(res.getTotal());
    dto.setTotalPages(res.getTotalPages());
    dto.setItems(res.getItems().stream().map(this::mapToDTO).toList());

    return dto;
  }

  /**
   * Retrieves a single comment by ID.
   *
   * @param id comment ID
   * @return CommentDTO.Out representing the comment
   * @throws RessourceNotFoundException if the comment does not exist
   */
  public CommentDTO.Out get(Long id){
    var res = this.commentDAO.get(id);

    if (res == null)
      throw  new RessourceNotFoundException("Comment not found");

   return this.mapToDTO(res);
  }

  /**
   * Retrieves all comments for a given post.
   *
   * @param postId ID of the post
   * @return list of CommentDTO.Out for the post
   */
  public List<CommentDTO.Out> getByPostId(Long postId){
    return this.commentDAO.findBy(String.valueOf(postId), CommentColumn.POST_ID)
            .stream().map(this::mapToDTO).toList();
  }

  /**
   * Retrieves all comments made by a user.
   *
   * @param userId ID of the user
   * @return list of Comment entities
   */
  public List<Comment> getByUserId(Long userId){
    return this.commentDAO.findBy(String.valueOf(userId), CommentColumn.USER_ID);
  }


  /**
   * Creates a new comment.
   *
   * @param in input DTO containing comment data
   * @return CommentDTO.Out representing the created comment
   */
  public CommentDTO.Out create(CommentDTO.In in){
    return  this.mapToDTO(this.commentDAO.create(this.mapToEntity(in)));
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
    Comment exist = this.commentDAO.get(id);
    if (exist == null)
      throw  new RessourceNotFoundException("Comment not found");
    exist.setBody(body);
    return this.mapToDTO(this.commentDAO.update(id, exist));
  }

  /**
   * Deletes a comment by ID.
   *
   * @param id ID of the comment to delete
   * @return true if deletion was successful
   */
  public boolean delete (Long id){
    return this.commentDAO.delete(id);
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
