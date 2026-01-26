package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dao.PostDAO;
import com.amalitech.blogging_platform.dto.PageRequest;
import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.PostDTO;
import com.amalitech.blogging_platform.exceptions.DataConflictException;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.Post;
import com.amalitech.blogging_platform.model.Tag;
import com.amalitech.blogging_platform.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service layer for managing posts.
 * <p>
 * Provides CRUD operations, search, and query post by authorId.
 * Converts between Post entities and PostDTOs.
 */
@Service
public class PostService {

  private final PostDAO postDAO;
  private final PostRepository postRepository;
  private final TagService tagService;
  private final PostTagsService postTagsService;
  private final Logger log = LoggerFactory.getLogger(PostService.class);

  @Autowired
  public PostService(PostRepository postRepository, PostDAO postDAO, TagService tagService, PostTagsService postTagsService) {
    this.postRepository = postRepository;
    this.postDAO = postDAO;
    this.tagService = tagService;
    this.postTagsService = postTagsService;

  }

  public PostDTO.Out create(PostDTO.In post){

    Post newPost =  this.postRepository.save(this.mapToEntity(post));

    post.getTags().forEach(name -> {
      log.debug("Tag name: {}", name);
      // TODO: check logic

      try{
      Tag t = this.tagService.create(name);
        this.postTagsService.create(newPost.getId(), t.getId());

      }catch(DataConflictException e){ // if Data conflict is thrown, ignore it and save the post tags
        Tag t = this.tagService.get(name);
        this.postTagsService.create(newPost.getId(), t.getId());
      }
    });
    return  this.mapToDTO(newPost);
  }

  public PostDTO.Out update(Long id, PostDTO.In post){

    return this.mapToDTO(this.postDAO.update(id, this.mapToEntity(post)));
  }

  public void delete(Long id){
    this.postRepository.deleteById(id);
  }

  // TODO: change to use data jpa
  public PaginatedData<PostDTO.Detailed> search(PageRequest pageRequest, String search, Long tagId, Long authorId){
    return this.postDAO.getPostDTOs(pageRequest.getPage(), pageRequest.getSize(), search, tagId, authorId, false );
  }

  public Page<PostDTO.Out> get (Pageable pageable){
    return this.postRepository.findAll(pageable).map(this::mapToDTO);

  }

  public PostDTO.Out get(Long id){
    return this.postRepository.findById(id).map(this::mapToDTO).orElseThrow( () -> new RessourceNotFoundException("Post not found"));
  }

  // TODO change to use data jpa
  public PostDTO.Detailed getDetailed(Long id){
    PostDTO.Detailed post = this.postDAO.getPostDTO(id, false);
    if(post == null){
      throw new RessourceNotFoundException("Post not found");
    }
    return post;
  }


  public Page<PostDTO.Out> getByAuthorId(Long id, Pageable pageable){
      return postRepository.findByAuthor_Id(id, pageable).map(this::mapToDTO);

  }

  private PostDTO.Out mapToDTO(Post post){
    PostDTO.Out dto = new PostDTO.Out();
    dto.setId(post.getId());
    dto.setTitle(post.getTitle());
    dto.setAuthorId(post.getAuthorId());
    dto.setBody(post.getBody());
    dto.setCreatedAt(post.getCreatedAt());
    dto.setUpdatedAt(post.getUpdatedAt());
    dto.setDeletedAt(post.getDeletedAt());
    dto.setDeleted(post.isDeleted());

    return dto;
  }

  private Post mapToEntity(PostDTO.In in){
    Post post = new Post();
    post.setAuthorId(in.getAuthorId());
    post.setTitle(in.getTitle());
    post.setBody(in.getBody());

    return post;
  }

}
