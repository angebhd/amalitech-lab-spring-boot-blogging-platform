package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.User;
import com.amalitech.blogging_platform.repository.CommentRepository;
import com.amalitech.blogging_platform.repository.PostRepository;
import com.amalitech.blogging_platform.repository.ReviewRepository;
import com.amalitech.blogging_platform.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  private final PasswordHashService passwordHashService;
  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final ReviewRepository reviewRepository;
  private static final String USERNOTFOUNDMESSAGE = "User not found";

  public UserService(UserRepository userRepository, PasswordHashService passwordHashService, PostRepository postRepository,
                     CommentRepository commentRepository, ReviewRepository reviewRepository) {
    this.userRepository = userRepository;
    this.passwordHashService = passwordHashService;
    this.postRepository = postRepository;
    this.commentRepository = commentRepository;
    this.reviewRepository = reviewRepository;
  }


  public UserDTO.Out create(UserDTO.In user){
    User createdUser = this.userRepository.save(this.mapToUser(user));
    return this.mapToUserDTO(createdUser);
  }

  @Cacheable(cacheNames = "users", key = "#id")
  public UserDTO.Out get(Long id){
    User response = this.userRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException(USERNOTFOUNDMESSAGE));
    return this.mapToUserDTO(response);
  }

  @Cacheable(cacheNames = "usersByUsername",  key = "#username.toLowerCase()")
  public UserDTO.Out getByUsername(String username){
    var response = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new RessourceNotFoundException(USERNOTFOUNDMESSAGE));
    return this.mapToUserDTO(response);
  }

  public PaginatedData<UserDTO.Out> get(Pageable pageable){
   return new PaginatedData<>(this.userRepository.findAll(pageable).map(UserDTO.Converter::toDTO));

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

    User oldUser = this.userRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException(USERNOTFOUNDMESSAGE));
    user.setPassword(oldUser.getPassword());

    if (user.getEmail() != null)
      oldUser.setEmail(user.getEmail());
    if (user.getUsername() != null)
      oldUser.setUsername(user.getUsername());
    if (user.getFirstName() != null)
      oldUser.setFirstName(user.getFirstName());
    if (user.getLastName() != null)
      oldUser.setLastName(user.getLastName());
    if (user.getRole() != null )
      oldUser.setRole(user.getRole());

    return this.mapToUserDTO(this.userRepository.save(oldUser));
  }


  @Transactional
  @Caching(
          evict = {
                  @CacheEvict(cacheNames = "users", key = "#id"),
                  @CacheEvict(cacheNames = "usersByUsername", allEntries = true)
          }
  )
  public void delete (Long id){
    User user = this.userRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException(USERNOTFOUNDMESSAGE));
    this.commentRepository.deleteByUser(user);
    this.postRepository.deleteByAuthor(user);
    this.reviewRepository.deleteByUser(user);
    this.userRepository.deleteById(id);
  }

  private User mapToUser(UserDTO.In in){
    User user = new User();
    String hashedPassword = this.passwordHashService.hash(in.getPassword().toCharArray());
    user.setFirstName(in.getFirstName());
    user.setLastName(in.getLastName());
    user.setUsername(in.getUsername());
    user.setEmail(in.getEmail());
    user.setPassword(hashedPassword);
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


}
