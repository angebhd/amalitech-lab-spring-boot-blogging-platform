package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dao.TagDAO;
import com.amalitech.blogging_platform.dto.PageRequest;
import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.exceptions.DataConflictException;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
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
  public PaginatedData<Tag> get(PageRequest pageRequest){
    return this.tagDAO.getAll(pageRequest.getPage(), pageRequest.getSize());
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
      throw new DataConflictException("Tag name already exists");
    }
   return this.tagDAO.create(t);
  }

  public Tag update(Long id, String name){
    Tag exist = this.tagDAO.get(id);
    if (exist == null){
      throw new RessourceNotFoundException("Tag id not found");
    }
    Tag existing = this.tagDAO.get(name);
    if (existing != null){
      throw new DataConflictException("Tag name already exists");
    }
    exist.setName(name);
    return this.tagDAO.update(id, exist);
  }

  public void delete(Long id){
    this.tagDAO.delete(id);
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
