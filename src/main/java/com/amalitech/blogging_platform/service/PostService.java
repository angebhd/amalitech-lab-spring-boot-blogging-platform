package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dao.PostDAO;
import com.amalitech.blogging_platform.dto.PageRequest;
import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.PostDTO;
import com.amalitech.blogging_platform.exceptions.DataConflictException;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.Post;
import com.amalitech.blogging_platform.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
  private final TagService tagService;
  private final PostTagsService postTagsService;
  private final Logger log = LoggerFactory.getLogger(PostService.class);

  @Autowired
  public PostService(PostDAO postDAO, TagService tagService, PostTagsService postTagsService) {
    this.postDAO = postDAO;
    this.tagService = tagService;
    this.postTagsService = postTagsService;

  }

  public PostDTO.Out create(PostDTO.In post){

    Post newPost =  this.postDAO.create(this.mapToEntity(post));

    post.getTags().forEach(name -> {
      log.debug("Tag name: {}", name);

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
    this.postDAO.delete(id);
  }

  public PaginatedData<PostDTO.Detailed> search(PageRequest pageRequest, String search, Long tagId, Long authorId){
    return this.postDAO.getPostDTOs(pageRequest.getPage(), pageRequest.getSize(), search, tagId, authorId, false );
  }

  public PaginatedData<PostDTO.Out> get (PageRequest pageRequest){
    PaginatedData<Post> res = this.postDAO.getAll(pageRequest.getPage(), pageRequest.getSize());

    return this.mapToDTO(res);
  }

  public PostDTO.Out get(Long id){
    var post = this.postDAO.get(id);
    if(post == null){
      throw new RessourceNotFoundException("Post not found");
    }
    return this.mapToDTO(post);
  }

  public PostDTO.Detailed getDetailed(Long id){
    PostDTO.Detailed post = this.postDAO.getPostDTO(id, false);
    if(post == null){
      throw new RessourceNotFoundException("Post not found");
    }
    return post;
  }

  public PaginatedData<PostDTO.Out> getByAuthorId(Long id){
    return this.getByAuthorId(id, new PageRequest(1,10) );
  }

  public PaginatedData<PostDTO.Out> getByAuthorId(Long id, PageRequest pageRequest){
      return this.mapToDTO(this.postDAO.getByAuthorId(id, pageRequest.getPage(), pageRequest.getSize()));

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

  private PaginatedData<PostDTO.Out> mapToDTO(PaginatedData<Post> posts){
    PaginatedData<PostDTO.Out> dto = new PaginatedData<>();
    dto.setPage(posts.getPage());
    dto.setPageSize(posts.getPageSize());
    dto.setTotal(posts.getTotal());
    dto.setTotalPages(posts.getTotalPages());
    dto.setItems(posts.getItems().stream().map(this::mapToDTO).toList());

    return dto;
  }
}
