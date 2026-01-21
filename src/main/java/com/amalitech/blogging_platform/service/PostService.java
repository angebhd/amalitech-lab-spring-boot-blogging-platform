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

import java.util.*;

@Service
public class PostService {

  private final PostDAO postDAO;
  private final TagService tagService;
  private final PostTagsService postTagsService;
  private final UserService userService;
  private final ReviewService reviewService;
  private final CommentService commentService;
  private final Logger log = LoggerFactory.getLogger(PostService.class);
  private Map<String, List<PostDTO>> cachedPostDTOs = new HashMap<>();
  private final Map<Long, List<Post>> cachedPostByAuthor = new HashMap<>();

  @Autowired
  public PostService(PostDAO postDAO, TagService tagService, PostTagsService postTagsService ,UserService userService, ReviewService reviewService, CommentService commentService) {
    this.postDAO = postDAO;
    this.tagService = tagService;
    this.postTagsService = postTagsService;
    this.userService = userService;
    this.reviewService = reviewService;
    this.commentService = commentService;
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

    this.cachedPostDTOs = new HashMap<>();
    this.cachedPostByAuthor.remove(post.getAuthorId());
    return  this.mapToDTO(newPost);
  }

  public PostDTO.Out update(Long id, PostDTO.In post){
    this.cachedPostByAuthor.remove(post.getAuthorId());
    return this.mapToDTO(this.postDAO.update(id, this.mapToEntity(post)));
  }

  public void delete(Long id){
    this.postDAO.delete(id);
  }

//  public List<PostDTO> loadFeed() {
//    return this.loadFeed(1, 20);
//  }
//    public List<PostDTO> loadFeed(int page, int pageSize){
//    List<Post> posts = this.postDAO.getAll(page, pageSize);
//    List<PostDTO> postDetails = new ArrayList<>();
//
//    posts.forEach(post -> {
//      PostDTO dto = new PostDTO();
//      dto.setPost(post);
//      var author = this.userService.get(post.getAuthorId());
//      dto.setAuthorId(author.getId());
//      dto.setAuthorName(author.getFirstName() + " " + author.getLastName());
//
//      List<Long> tagsId = this.postTagsService.getTagsIdByPostId(post.getId());
//      List<Tag> tags = new ArrayList<>();
//      tagsId.forEach(a -> tags.add(this.tagService.get(a)) );
//      dto.setTags(tags);
//
//      dto.setReviews(this.reviewService.getByPostId(post.getId()));
//      this.commentService.getByPostId(post.getId());
//      postDetails.add(dto);
//    });
//      this.cachedPostDTOs = new HashMap<>();
//    return  postDetails;
//  }

//  public List<PostDTO> loadFeed(boolean withPerformance) {
//    return loadFeed(1, 20, withPerformance);
//  }
//
//  public List<PostDTO> loadFeed(int page, int pageSize, boolean withPerformance){
//    if (!withPerformance)
//      return loadFeed();
//    if(this.cachedPostDTOs.get(this.makeCacheKeyforLoadFeed(page, pageSize))!= null)
//      return this.cachedPostDTOs.get(this.makeCacheKeyforLoadFeed(page, pageSize));
//
//    var res = this.postDAO.getPostDTOs(page, pageSize, null, null, null, false );
//    this.cachedPostDTOs.put(makeCacheKeyforLoadFeed(page, pageSize), res);
//    return res;
//  }

  public List<PostDTO> search(int page, int pageSize, String search, Long tagId){
    return this.postDAO.getPostDTOs(page, pageSize, search, tagId, null, false );
  }

//  public PostDTO loadById(Long id){
//    Post post = this.postDAO.get(id);
//
//    PostDTO dto = new PostDTO();
//    dto.setPost(post);
//    var author = this.userService.get(post.getAuthorId());
//    dto.setAuthorName(author.getFirstName() + " " + author.getLastName());
//    List<Long> tagsId = this.postTagsService.getTagsIdByPostId(post.getId());
//    List<Tag> tags = new ArrayList<>();
//    tagsId.forEach(a -> tags.add(this.tagService.get(a)) );
//    dto.setTags(tags);
//
//    dto.setReviews(this.reviewService.getByPostId(post.getId()));
//    this.commentService.getByPostId(post.getId());
//
//    return  dto;
//  }

  public Post getById(Long id){
    return this.postDAO.get(id);
  }

  public PaginatedData<PostDTO.Out> get (PageRequest pageRequest){
    PaginatedData<Post> res = this.postDAO.getAll(pageRequest.getPage(), pageRequest.getSize());
    PaginatedData<PostDTO.Out> dto = new PaginatedData<>();
    dto.setPage(res.getPage());
    dto.setPageSize(res.getPageSize());
    dto.setTotal(res.getTotal());
    dto.setTotalPages(res.getTotalPages());
    dto.setItems(res.getItems().stream().map(this::mapToDTO).toList());

    return dto;
  }

  public PostDTO.Out get(Long id){
    var post = this.postDAO.get(id);
    if(post == null){
      throw new RessourceNotFoundException("Post not found");
    }
    return this.mapToDTO(post);
  }

  public List<Post> getByAuthorId(Long id){
    return this.postDAO.getByAuthorId(id);
  }

  public List<Post> getByAuthorId(Long id, boolean withPerformance){
    if(!withPerformance)
      return this.postDAO.getByAuthorId(id);

    if ( cachedPostByAuthor.get(id) == null) {
      var posts = this.postDAO.getByAuthorId(id);
      cachedPostByAuthor.put(id, posts);
    }
    return this.cachedPostByAuthor.get(id);
  }

  private String makeCacheKeyforLoadFeed(int page, int pageSize){
    return page+"~"+pageSize;
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
