package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dao.ReviewDAO;
import com.amalitech.blogging_platform.dto.ReviewDTO;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.EReview;
import com.amalitech.blogging_platform.model.Review;
import com.amalitech.blogging_platform.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

  private final ReviewDAO reviewDAO;
  private final ReviewRepository reviewRepository;
  @Autowired
  public ReviewService(ReviewRepository repository ,ReviewDAO reviewDAO) {
    this.reviewRepository = repository;
    this.reviewDAO = reviewDAO;
  }

  public Page<Review> get(Pageable pageable) {
    return this.reviewRepository.findAll(pageable);
  }

  public Review get(Long id) {
   return this.reviewRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("Review not found"));
  }


  public Page<Review> getByPostId(Long postId){
    return reviewRepository.findByPost_Id(postId, Pageable.unpaged());
  }

  public Page<Review> getByUserId(Long userId){
    return reviewRepository.findByUser_Id(userId, Pageable.unpaged());
  }

  public Review create(ReviewDTO.In review){
    return this.reviewRepository.save(this.mapToReview((review)));
  }

  public Review update(Long id, EReview eReview){
    Review review = this.reviewRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("Review not found"));
    review.setRate(eReview.name());
    return this.reviewRepository.save(review);
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
