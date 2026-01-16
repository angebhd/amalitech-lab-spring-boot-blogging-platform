package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dao.PostTagsDAO;
import com.amalitech.blogging_platform.model.PostTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PostTagsService {
  private final PostTagsDAO postTagsDAO;
  private final Logger log = LoggerFactory.getLogger(PostTagsService.class);

  @Autowired
  public PostTagsService(PostTagsDAO postTagsDAO){
    this.postTagsDAO = postTagsDAO;
  }

  public PostTags create (Long postId, Long tagId){
    log.debug("Creating posttag for post: {}", postId);
    log.debug("Creating posttag for tag: {}", tagId);
    PostTags postTags = new PostTags();
    postTags.setPostId(postId);
    postTags.setTagId(tagId);

    this.postTagsDAO.create(postTags);
    return postTags;
  }
  public List<Long> getTagsIdByPostId(Long postId){
    return this.postTagsDAO.findTagIdsByPost(postId);

  }
  public List<Long> getPostIdByTagId(Long tagId){
    return this.postTagsDAO.findPostIdsByTag(tagId);
  }

  public void deletePostTags(Long postId){
    this.postTagsDAO.deleteByPost(postId);
  }

  public List<Long> getTopTagsId(int limit){
    return this.postTagsDAO.findTopTagsId(limit);
  }
}
