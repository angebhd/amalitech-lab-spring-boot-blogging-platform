package com.amalitech.blogging_platform.service;


import com.amalitech.blogging_platform.dao.CommentDAO;
import com.amalitech.blogging_platform.dao.PostDAO;
import com.amalitech.blogging_platform.dao.UserDAO;
import com.amalitech.blogging_platform.dao.enums.CommentColumn;
import com.amalitech.blogging_platform.dao.enums.UserColumn;
import com.amalitech.blogging_platform.dto.PageRequest;
import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
  private final PasswordHashService passwordHashService;
  private final UserDAO userDAO;
  private final PostDAO postDAO;
  private final CommentDAO commentDAO;
  private final ReviewService reviewService;

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
    User response = this.userDAO.get(id);
    if(response == null)
      throw new RessourceNotFoundException("User not found");
    return this.mapToUserDTO(response);
  }

  public UserDTO.Out getByUsername(String username){
    var response = this.userDAO.getBy(username, UserColumn.USERNAME, false);

    if(response == null)
      throw new RessourceNotFoundException("User not found");
    return this.mapToUserDTO(response);  }

  public PaginatedData<UserDTO.Out> get(PageRequest pageRequest){
    PaginatedData<User> response = this.userDAO.getAll(pageRequest.getPage(), pageRequest.getSize());
    PaginatedData<UserDTO.Out> paginatedData = new PaginatedData<>();
    paginatedData.setItems(response.getItems().stream().map(this::mapToUserDTO).toList());
    paginatedData.setPage(response.getPage());
    paginatedData.setPageSize(response.getPageSize());
    paginatedData.setTotal(response.getTotal());
    paginatedData.setTotalPages(response.getTotalPages());
    return paginatedData;
  }


  public UserDTO.Out update(Long id, UserDTO.In user){
    User oldUser = this.userDAO.get(id);
    user.setPassword(oldUser.getPassword());
    return this.mapToUserDTO(this.userDAO.update(id, this.mapToUser(user)));
  }


  public boolean delete (Long id){
    return this.userDAO.delete(id);
  }

  public Map<String, Integer> getUserStats(Long userId){
    Map<String, Integer> response = new HashMap<>();
    int postsCount = postDAO.getByAuthorId(userId, 1, Integer.MAX_VALUE).getItems().size();
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
