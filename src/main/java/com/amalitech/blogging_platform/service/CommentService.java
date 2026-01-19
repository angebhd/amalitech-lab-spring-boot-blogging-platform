package com.amalitech.blogging_platform.service;



import com.amalitech.blogging_platform.dao.CommentDAO;
import com.amalitech.blogging_platform.dao.enums.CommentColumn;
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

  public List<Comment> getByPostId(Long postId){
    return this.commentDAO.findBy(String.valueOf(postId), CommentColumn.POST_ID);
  }

  public List<Comment> getByUserId(Long postId){
    return this.commentDAO.findBy(String.valueOf(postId), CommentColumn.USER_ID);
  }

  public Comment create(Comment entity){
    return  this.commentDAO.create(entity);
  }

  public Comment update (Long id, Comment entity){
    return this.commentDAO.update(id, entity);
  }

  public boolean delete (Long id){
    return this.commentDAO.delete(id);
  }


}
