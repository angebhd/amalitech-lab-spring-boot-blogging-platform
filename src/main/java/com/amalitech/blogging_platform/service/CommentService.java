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
@Service
public class CommentService {
  private final CommentDAO commentDAO;

  @Autowired
  public CommentService(CommentDAO commentDAO){
    this.commentDAO = commentDAO;
  }

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

  public CommentDTO.Out get(Long id){
    var res = this.commentDAO.get(id);

    if (res == null)
      throw  new RessourceNotFoundException("Comment not found");

   return this.mapToDTO(res);
  }



  public List<Comment> getByPostId(Long postId){
    return this.commentDAO.findBy(String.valueOf(postId), CommentColumn.POST_ID);
  }

  public List<Comment> getByUserId(Long postId){
    return this.commentDAO.findBy(String.valueOf(postId), CommentColumn.USER_ID);
  }

  public CommentDTO.Out create(CommentDTO.In in){
    return  this.mapToDTO(this.commentDAO.create(this.mapToEntity(in)));
  }

  public CommentDTO.Out update (Long id, String body){
    Comment exist = this.commentDAO.get(id);
    if (exist == null)
      throw  new RessourceNotFoundException("Comment not found");
    exist.setBody(body);
    return this.mapToDTO(this.commentDAO.update(id, exist));
  }

  public boolean delete (Long id){
    return this.commentDAO.delete(id);
  }

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

  private Comment mapToEntity(CommentDTO.In in){
    Comment entity = new Comment();
    entity.setPostId(in.getPostId());
    entity.setUserId(in.getUserId());
    entity.setBody(in.getBody());
    entity.setParentCommentId(in.getParentCommentId());

    return entity;
  }


}
