package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dao.ReviewDAO;
import com.amalitech.blogging_platform.dto.PageRequest;
import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.ReviewDTO;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.EReview;
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

  public PaginatedData<Review> get(PageRequest pageRequest) {
    return this.reviewDAO.getAll(pageRequest.getPage(), pageRequest.getSize());
  }

  public Review get(Long id) {
    var review = this.reviewDAO.get(id);
    if(review == null)
      throw new RessourceNotFoundException("Review not found");

    return this.reviewDAO.get(id);
  }


  public List<Review> getByPostId(Long postId){
    return this.reviewDAO.getByPostId(postId);
  }

  public List<Review> getByUserId(Long userId){
    return this.reviewDAO.getByUserId(userId);
  }

  public Review create(ReviewDTO.In review){
    return this.reviewDAO.create(this.mapToReview((review)));
  }

  public Review update(Long id, EReview eReview){
    Review review = this.reviewDAO.get(id);
    if(review == null){
      throw new RessourceNotFoundException("Review not found");
    }
    review.setRate(eReview.name());
    return this.reviewDAO.update(id, review);
  }

  public boolean delete(Long id){
    return this.reviewDAO.delete(id);
  }

  private Review mapToReview(ReviewDTO.In dto){
    Review review = new Review();
    review.setUserId(dto.getUserId());
    review.setPostId(dto.getPostId());
    review.setRate(dto.getRate().name());
    return review;
  }
}
