package com.amalitech.blogging_platform.service;


import com.amalitech.blogging_platform.dao.ReviewDAO;
import com.amalitech.blogging_platform.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ReviewService {

  private final ReviewDAO reviewDAO;
  @Autowired
  public ReviewService(ReviewDAO reviewDAO) {
    this.reviewDAO = reviewDAO;
  }

  public List<Review> getByPostId(Long postId){
    return this.reviewDAO.getByPostId(postId);
  }

  public List<Review> getByUserId(Long userId){
    return this.reviewDAO.getByUserId(userId);
  }

  public Review create(Review review){
    return this.reviewDAO.create(review);
  }

  public Review update(Long id, Review review){
    return this.reviewDAO.update(id, review);
  }

  public boolean delete(Long id){
    return this.reviewDAO.delete(id);
  }

}
