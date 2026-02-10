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
import com.amalitech.blogging_platform.repository.CommentRepository;
import com.amalitech.blogging_platform.repository.PostRepository;
import com.amalitech.blogging_platform.repository.ReviewRepository;
import com.amalitech.blogging_platform.repository.UserRepository;
import com.amalitech.blogging_platform.repository.projections.PostWithStatsProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  private final CommentRepository commentRepository;
  private final ReviewRepository reviewRepository;
  private final ModerationService moderationService;
  private static final String POST_NOT_FOUND = "Post not Found";

  @Autowired
  public PostService(PostRepository postRepository, UserRepository userRepository, TagService tagService,
                     CommentRepository commentRepository, ReviewRepository reviewRepository, ModerationService moderationService) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
    this.tagService = tagService;
    this.commentRepository = commentRepository;
    this.reviewRepository = reviewRepository;
    this.moderationService = moderationService;

  }

  @Transactional
//  @CachePut(cacheNames = "posts", key = "#result.id")
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
    this.moderationService.validatePost(savedPost);
    return  this.mapToDTO(savedPost);
  }

  @Transactional
  @CachePut(cacheNames = "posts", key = "#id")
  public PostDTO.Out update(Long id, PostDTO.In post){
    if (post.getTags().size() > 5)
      throw new BadRequestException("Post cannot have more than 5 tags");

    Post oldPost = this.postRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException(POST_NOT_FOUND));
    if(post.getTitle() != null)
      oldPost.setTitle(post.getTitle());
    if (oldPost.getBody() != null)
      oldPost.setBody(post.getBody());

    if (!oldPost.getTags().isEmpty()){
      List<Tag> tags = this.tagService.getOrCreateTags(post.getTags().stream().toList());
      oldPost.setTags(tags);
    }

    Post savedPost = this.postRepository.save(oldPost);
    this.moderationService.validatePost(savedPost);
    return this.mapToDTO(savedPost);
  }

  @Async
  @Transactional
  @CacheEvict(cacheNames = {"posts"}, key = "#id")
  public void delete(Long id){
    Post post = this.postRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException(POST_NOT_FOUND));
    this.reviewRepository.deleteByPost(post);
    this.commentRepository.deleteByPost(post);
    this.postRepository.delete(post);
  }

  public PaginatedData<PostDTO.OutWithStats> search(String keyword, Pageable pageable){
    Post post = new Post();
    post.setTitle(keyword);
    post.setTitle(keyword);
    return new PaginatedData<>(this.postRepository.searchWithStats(keyword, pageable).map(this::mapToWithStats));
  }

  public PaginatedData<PostDTO.Out> get (Pageable pageable){
    var data = this.postRepository.findAll(pageable).map(this::mapToDTO);
    return new PaginatedData<>(data);
  }

  public PaginatedData<PostDTO.OutWithStats> getFeed(Pageable pageable){
    return new PaginatedData<>(this.postRepository.findAllWithStats(pageable).map(this::mapToWithStats));
  }
  @Cacheable(cacheNames = "posts", key = "#id")
  public PostDTO.Out get(Long id){
    return this.postRepository.findById(id).map(this::mapToDTO).orElseThrow( () -> new RessourceNotFoundException(POST_NOT_FOUND));
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

  private PostDTO.OutWithStats mapToWithStats(PostWithStatsProjection p) {

    var dto = new PostDTO.OutWithStats();

    dto.setId(p.getId());
    dto.setTitle(p.getTitle());
    dto.setBody(p.getBody());
    dto.setCreatedAt(p.getCreatedAt());
    dto.setUpdatedAt(p.getUpdatedAt());
    dto.setDeletedAt(p.getDeletedAt());
    dto.setDeleted(p.getIsDeleted());

    dto.setReviews(p.getReviews());
    dto.setReviewAverage(p.getReviewAverage().floatValue());
    dto.setComments(p.getComments());

    dto.setTags(List.of(p.getTags()));

    var author = new UserDTO.Out();
    author.setId(p.getAuthorId());
    author.setUsername(p.getAuthorUsername());
    author.setEmail(p.getAuthorEmail());
    author.setFirstName(p.getAuthorFirstName());
    author.setFirstName(p.getAuthorFirstName());

    dto.setAuthor(author);

    return dto;
  }


}
