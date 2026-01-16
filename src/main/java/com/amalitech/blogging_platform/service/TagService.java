package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dao.TagDAO;
import com.amalitech.blogging_platform.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {
  private final TagDAO tagDAO;
  private final PostTagsService postTagsService;

  @Autowired
  public TagService(TagDAO tagDAO, PostTagsService postTagsService) {
    this.tagDAO = tagDAO;
    this.postTagsService = postTagsService;
  }
  public List<Tag> getAll(){
    return this.tagDAO.getAll();
  }


  public Tag get(Long id){
    return this.tagDAO.get(id);
  }

  public Tag get(String name){
    return this.tagDAO.get(name);
  }

  public Tag create(String name){
    Tag exist = this.tagDAO.get(name);
    Tag t = new Tag();
    t.setName(name);
    if (exist != null){
      return exist;
    }
   return this.tagDAO.create(t);
  }

  public void updatePostTags(Long postId, List<String> tags){
    this.postTagsService.deletePostTags(postId);
    tags.forEach(tagName -> {
      Tag tag = this.create(tagName);
      this.postTagsService.create(postId, tag.getId());
    });
  }

  public List<Tag> getTop(int limit){
    List<Tag> tags = new ArrayList<>(limit);
    this.postTagsService.getTopTagsId(limit).forEach(e -> tags.add(this.get(e)));
    return tags;
  }

}
