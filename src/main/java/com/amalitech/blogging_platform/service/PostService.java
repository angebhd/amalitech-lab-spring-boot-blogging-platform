package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.PostDTO;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.exceptions.BadRequestException;
import com.amalitech.blogging_platform.exceptions.DataConflictException;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.Post;
import com.amalitech.blogging_platform.model.Tag;
import com.amalitech.blogging_platform.model.User;
import com.amalitech.blogging_platform.repository.PostRepository;
import com.amalitech.blogging_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for managing posts.
 * <p>
 * Provides CRUD operations, search, and query post by authorId.
 * Converts between Post entities and PostDTOs.
 */
@Service
public class PostService {

  private final PostRepository postRepository;
  private final TagService tagService;
  private final UserRepository userRepository;

  @Autowired
  public PostService(PostRepository postRepository, UserRepository userRepository, TagService tagService ) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
    this.tagService = tagService;

  }

  public PostDTO.Out create(PostDTO.In postIn){
    Post post = this.mapToEntity(postIn);

    if(postIn.getTags().size() > 5)
      throw new BadRequestException("Post cannot have more than 5 tags");

    List<Tag> tags = this.tagService.getOrCreateTags(postIn.getTags().stream().toList());
    User author = userRepository.findById(postIn.getAuthorId())
            .orElseThrow(()-> new DataConflictException("Author with id: " +postIn.getAuthorId() + " not found"));

    post.setTags(tags);
    post.setAuthor(author);
    Post savedPost =  this.postRepository.save(post);

    return  this.mapToDTO(savedPost);
  }

  public PostDTO.Out update(Long id, PostDTO.In post){
    if (post.getTags().size() > 5)
      throw new BadRequestException("Post cannot have more than 5 tags");

    Post oldPost = this.postRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("Post not found"));
    if(post.getTitle() != null)
      oldPost.setTitle(post.getTitle());
    if (oldPost.getBody() != null)
      oldPost.setBody(post.getBody());

    if (!oldPost.getTags().isEmpty()){
      List<Tag> tags = this.tagService.getOrCreateTags(post.getTags().stream().toList());
      oldPost.setTags(tags);
    }

    return this.mapToDTO(this.postRepository.save(oldPost));
  }

  public void delete(Long id){
    this.postRepository.deleteById(id);
  }

  // TODO: correct this
  public PaginatedData<PostDTO.Out> search(String keyword, Pageable pageable){
    Post post = new Post();
    post.setTitle(keyword);
    post.setTitle(keyword);
    return new PaginatedData<>(this.postRepository.findAll(Example.of(post), pageable).map(this::mapToDTO));
  }

  public PaginatedData<PostDTO.Out> get (Pageable pageable){
    return new PaginatedData<>(this.postRepository.findAll(pageable).map(this::mapToDTO));

  }

  public PostDTO.Out get(Long id){
    return this.postRepository.findById(id).map(this::mapToDTO).orElseThrow( () -> new RessourceNotFoundException("Post not found"));
  }



  public PaginatedData<PostDTO.Out> getByAuthorId(Long id, Pageable pageable){
      return new PaginatedData<>(postRepository.findByAuthor_Id(id, pageable).map(this::mapToDTO));

  }

  private PostDTO.Out mapToDTO(Post post){
    PostDTO.Out dto = new PostDTO.Out();
    dto.setId(post.getId());
    dto.setTitle(post.getTitle());
    dto.setAuthor(UserDTO.Converter.toDTO(post.getAuthor()));
    dto.setBody(post.getBody());
    dto.setCreatedAt(post.getCreatedAt());
    dto.setUpdatedAt(post.getUpdatedAt());
    dto.setDeletedAt(post.getDeletedAt());
    dto.setDeleted(post.isDeleted());

    return dto;
  }

  private Post mapToEntity(PostDTO.In in){
    Post post = new Post();
    post.setTitle(in.getTitle());
    post.setBody(in.getBody());
    return post;
  }

}
