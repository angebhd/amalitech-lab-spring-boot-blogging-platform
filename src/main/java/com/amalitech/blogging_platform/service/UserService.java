package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.User;
import com.amalitech.blogging_platform.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final PasswordHashService passwordHashService;
  private final UserRepository userRepository;
  private static final String USERNOTFOUNDMESSAGE = "User not found";

  public UserService(UserRepository userRepository, PasswordHashService passwordHashService) {
    this.userRepository = userRepository;
    this.passwordHashService = passwordHashService;
  }


  public UserDTO.Out create(UserDTO.In user){
    User createdUser = this.userRepository.save(this.mapToUser(user));
    return this.mapToUserDTO(createdUser);
  }

  public UserDTO.Out get(Long id){
    User response = this.userRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException(USERNOTFOUNDMESSAGE));
    return this.mapToUserDTO(response);
  }

  public UserDTO.Out getByUsername(String username){
    var response = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new RessourceNotFoundException(USERNOTFOUNDMESSAGE));
    return this.mapToUserDTO(response);
  }

  public Page<UserDTO.Out> get(Pageable pageable){
    Page<User> response = this.userRepository.findAll(pageable);
    return response.map(this::mapToUserDTO);

  }


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


  public void delete (Long id){
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
