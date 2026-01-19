package com.amalitech.blogging_platform.service;


import com.amalitech.blogging_platform.dao.CommentDAO;
import com.amalitech.blogging_platform.dao.PostDAO;
import com.amalitech.blogging_platform.dao.UserDAO;
import com.amalitech.blogging_platform.dao.enums.CommentColumn;
import com.amalitech.blogging_platform.dao.enums.UserColumn;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
  private final PasswordHashService passwordHashService;
  private final UserDAO userDAO;
  private final PostDAO postDAO;
  private final CommentDAO commentDAO;
  private final ReviewService reviewService;
  private final Logger log = LoggerFactory.getLogger(UserService.class);

  public UserService(PasswordHashService passwordHashService, UserDAO userDAO, PostDAO postDAO, CommentDAO commentDAO, ReviewService reviewService) {
    this.passwordHashService = passwordHashService;
    this.userDAO = userDAO;
    this.postDAO = postDAO;
    this.commentDAO = commentDAO;
    this.reviewService = reviewService;
  }


  public UserDTO.Out create(UserDTO.In user){
    User createdUser = this.userDAO.create(this.mapToUser(user));

    return this.mapToUserDTO(createdUser);
  }

  public UserDTO.Out get(Long id){
    return this.mapToUserDTO(this.userDAO.get(id));
  }

  public User login(String username, String password){
    Optional<User> user = this.userDAO.findOneBy(username, UserColumn.USERNAME);
    if (user.isPresent()){
      boolean match = this.passwordHashService.verify(password.toCharArray(), user.get().getPassword());
      user.get().setPassword(null);
      if (match)
        return user.get();
    }
    return null;
  }

  public User update(Long id, User user){
    User oldUser = this.userDAO.get(id);
    user.setPassword(oldUser.getPassword());
    return this.userDAO.update(id, user);
  }

  public User updatePassword(Long userId, String oldPassword, String newPassword){
    log.info("Update Password | new: {}", newPassword);
    log.info("Update Password | old: {}", oldPassword);
    User user = this.userDAO.get(userId);
    if (this.passwordHashService.verify(oldPassword.toCharArray(), user.getPassword())) {
      user.setPassword(this.passwordHashService.hash(newPassword.toCharArray()));
      return this.userDAO.update(userId, user);
    }
    throw new RuntimeException("Invalid password");
  }

  public boolean delete (Long id){
    return  this.userDAO.delete(id);
  }

  public Map<String, Integer> getUserStats(Long userId){
    Map<String, Integer> response = new HashMap<>();
    int postsCount = postDAO.getByAuthorId(userId).size();
    response.put("postCount", postsCount);

    int commentsCount = this.commentDAO.findBy(String.valueOf(userId), CommentColumn.USER_ID, false).size();
    response.put("commentsCount", commentsCount);

    int reviewsCount = this.reviewService.getByUserId(userId).size();
    response.put("reviewsCount", reviewsCount);

    return response;
  }

  public Map<String, Integer> getUserStats(Long userId, boolean withPerformance){
    if (!withPerformance)
      return this.getUserStats(userId);

    return this.userDAO.getUserStats(userId);
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
