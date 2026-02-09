package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.exceptions.DataConflictException;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.EReview;
import com.amalitech.blogging_platform.model.Review;
import com.amalitech.blogging_platform.model.User;
import com.amalitech.blogging_platform.model.UserRole;
import com.amalitech.blogging_platform.repository.CommentRepository;
import com.amalitech.blogging_platform.repository.PostRepository;
import com.amalitech.blogging_platform.repository.ReviewRepository;
import com.amalitech.blogging_platform.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final ReviewRepository reviewRepository;
  private static final String USER_NOT_FOUND_MESSAGE = "User not found";

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, PostRepository postRepository,
                     CommentRepository commentRepository, ReviewRepository reviewRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.postRepository = postRepository;
    this.commentRepository = commentRepository;
    this.reviewRepository = reviewRepository;
  }

  public UserDTO.Out create(UserDTO.In user) {
    return this.create(user, false);
  }

  public UserDTO.Out create(UserDTO.In user, boolean isAdmin) {
    verifyExistingUsername(user.getUsername());
    verifyExistingEmail(user.getEmail());
    UserRole role = isAdmin ? UserRole.ADMIN : UserRole.USER;
    User createdUser = this.userRepository.save(this.mapToUser(user, role));
    return this.mapToUserDTO(createdUser);
  }

  @Cacheable(cacheNames = "users", key = "#id")
  public UserDTO.Out get(Long id){
    User response = this.userRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException(USER_NOT_FOUND_MESSAGE));
    return this.mapToUserDTO(response);
  }

  @Cacheable(cacheNames = "usersByUsername",  key = "#username.toLowerCase()")
  public UserDTO.Out getByUsername(String username){
    var response = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new RessourceNotFoundException(USER_NOT_FOUND_MESSAGE));
    return this.mapToUserDTO(response);
  }

  public PaginatedData<UserDTO.Out> get(Pageable pageable){
   return new PaginatedData<>(this.userRepository.findAll(pageable).map(UserDTO.Converter::toDTO));
  }


  @Async
  public CompletableFuture<UserDTO.UserStat> userStats(Long userId){
    var user = this.userRepository.findById(userId).orElseThrow(() -> new RessourceNotFoundException(USER_NOT_FOUND_MESSAGE));

    CompletableFuture<Long> posts = CompletableFuture.supplyAsync(() -> this.postRepository.countByAuthor(user));
    CompletableFuture<Long> comments = CompletableFuture.supplyAsync(() -> this.commentRepository.countByUser(user));
    CompletableFuture<Long> reviews = CompletableFuture.supplyAsync(() -> this.reviewRepository.countByUser(user));
    CompletableFuture<List<Review>> postReviews = CompletableFuture.supplyAsync(() -> reviewRepository.findByUser(user));

    return CompletableFuture.allOf(posts, comments, reviews, postReviews).thenApply( res ->
            new UserDTO.UserStat(posts.join(), comments.join(), reviews.join(), this.averageReview(postReviews.join()))
            );
  }


  @Caching(
          put = {
                  @CachePut(cacheNames = "users", key = "#id"),
                  @CachePut(cacheNames = "usersByUsername", key = "#result.username.toLowerCase()")
          },
          evict = {
                  @CacheEvict(cacheNames = "usersByUsername", allEntries = true),
          }
  )
  public UserDTO.Out update(Long id, UserDTO.In user){
    User oldUser = this.userRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException(USER_NOT_FOUND_MESSAGE));
    user.setPassword(oldUser.getPassword());

    if (user.getEmail() != null && !user.getEmail().equals(oldUser.getEmail())) {
      verifyExistingEmail(user.getEmail());
      oldUser.setEmail(user.getEmail());
      }
    if (user.getUsername() != null && !user.getUsername().equals(oldUser.getUsername())) {
      verifyExistingUsername(user.getUsername());
      oldUser.setUsername(user.getUsername());
    }

    if (user.getFirstName() != null)
      oldUser.setFirstName(user.getFirstName());
    if (user.getLastName() != null)
      oldUser.setLastName(user.getLastName());

    return this.mapToUserDTO(this.userRepository.save(oldUser));
  }

  public UserDTO.Out makeAdmin(long id){
    User user = this.userRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException(USER_NOT_FOUND_MESSAGE));
    user.setRole(UserRole.ADMIN);
    return this.mapToUserDTO(this.userRepository.save(user));
  }

  public UserDTO.Out removeAdmin(long id){
    User user = this.userRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException(USER_NOT_FOUND_MESSAGE));
    user.setRole(UserRole.USER);
    return this.mapToUserDTO(this.userRepository.save(user));
  }


  @Transactional
  @Caching(
          evict = {
                  @CacheEvict(cacheNames = "users", key = "#id"),
                  @CacheEvict(cacheNames = "usersByUsername", allEntries = true)
          }
  )
  public void delete (Long id){
    User user = this.userRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException(USER_NOT_FOUND_MESSAGE));
    this.commentRepository.deleteByUser(user);
    this.postRepository.deleteByAuthor(user);
    this.reviewRepository.deleteByUser(user);
    this.userRepository.deleteById(id);
  }

  private void verifyExistingUsername(String username){
    boolean existByUsername = userRepository.existsByUsernameIgnoreCase(username);
    if(existByUsername)
      throw new DataConflictException("Username already exist");
  }

  private void verifyExistingEmail(String email){
    boolean existByEmail = this.userRepository.existsByEmailIgnoreCase(email);
    if(existByEmail)
      throw new DataConflictException("Email already exist");
  }

  private User mapToUser(UserDTO.In in, UserRole role){
    User user = new User();
    String hashedPassword = this.passwordEncoder.encode(in.getPassword());
    user.setFirstName(in.getFirstName());
    user.setLastName(in.getLastName());
    user.setUsername(in.getUsername());
    user.setEmail(in.getEmail());
    user.setPassword(hashedPassword);
    user.setRole(role);
    return user;
  }

    private UserDTO.Out mapToUserDTO(User user){
    UserDTO.Out out = new UserDTO.Out();
    out.setId(user.getId());
    out.setFirstName(user.getFirstName());
    out.setLastName(user.getLastName());
    out.setUsername(user.getUsername());
    out.setEmail(user.getEmail());
    out.setCreatedAt(user.getCreatedAt());
    out.setUpdatedAt(user.getUpdatedAt());
    out.setDeletedAt(user.getDeletedAt());
    out.setDeleted(user.isDeleted());
    return out;
  }

  private float averageReview(List<Review> reviews){
    return (reviews.stream()
            .map(r -> this.mapReviewNumber(EReview.valueOf(r.getRate())))
            .reduce(Float::sum)
            .orElse(0f)) / reviews.size();

  }

  private float mapReviewNumber(EReview review){
    return switch (review){
      case EReview.ONE -> 1;
      case EReview.TWO -> 2;
      case EReview.THREE -> 3;
      case EReview.FOUR -> 4;
      case EReview.FIVE -> 5;
      default -> 0;
    };
  }


}
