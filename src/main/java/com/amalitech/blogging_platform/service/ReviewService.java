package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.ReviewDTO;
import com.amalitech.blogging_platform.exceptions.BadRequestException;
import com.amalitech.blogging_platform.exceptions.DataConflictException;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.EReview;
import com.amalitech.blogging_platform.model.Review;
import com.amalitech.blogging_platform.repository.PostRepository;
import com.amalitech.blogging_platform.repository.ReviewRepository;
import com.amalitech.blogging_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  @Autowired
  public ReviewService(ReviewRepository repository, PostRepository postRepository, UserRepository userRepository) {
    this.postRepository = postRepository;
    this.reviewRepository = repository;
    this.userRepository = userRepository;
  }

  public PaginatedData<ReviewDTO.Out> get(Pageable pageable) {
    return new PaginatedData<>(this.reviewRepository.findAll(pageable).map(ReviewDTO.Converter::toDTO));
  }

  public ReviewDTO.Out get(Long id) {
   return this.reviewRepository.findById(id).map(ReviewDTO.Converter::toDTO).orElseThrow(() -> new RessourceNotFoundException("Review not found"));
  }


  public PaginatedData<ReviewDTO.Out> getByPostId(Long postId){
    return new PaginatedData<>(reviewRepository.findByPost_Id(postId, Pageable.unpaged()).map(ReviewDTO.Converter::toDTO));
  }

  public PaginatedData<ReviewDTO.Out> getByUserId(Long userId){
    return new PaginatedData<>(reviewRepository.findByUser_Id(userId, Pageable.unpaged()).map(ReviewDTO.Converter::toDTO));
  }

  public ReviewDTO.Out create(ReviewDTO.In in){

    var review = this.mapToReview((in));

    var post = this.postRepository.findById(in.getPostId()).orElseThrow(() -> new DataConflictException("Post not found, with teh given ID"));
    var user = this.userRepository.findById(in.getUserId()).orElseThrow(() -> new DataConflictException("User not found, with the given ID"));

    review.setPost(post);
    review.setUser(user);

    return ReviewDTO.Converter.toDTO(this.reviewRepository.save(review));
  }

  public ReviewDTO.Out update(Long id, EReview eReview){
    if (eReview == null)
      throw new BadRequestException("Review cannot be null !");

    Review review = this.reviewRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("Review not found"));
    review.setRate(eReview.name());
    return ReviewDTO.Converter.toDTO(this.reviewRepository.save(review));
  }

  public void delete(Long id){
    this.reviewRepository.deleteById(id);
  }

  private Review mapToReview(ReviewDTO.In dto){
    Review review = new Review();
    review.setUserId(dto.getUserId());
    review.setPostId(dto.getPostId());
    review.setRate(dto.getRate().name());
    return review;
  }

}
