package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.exceptions.DataConflictException;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.Tag;
import com.amalitech.blogging_platform.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {

  private final TagRepository tagRepository;
  private final PostTagsService postTagsService;

  @Autowired
  public TagService(TagRepository tagRepository, PostTagsService postTagsService) {
    this.tagRepository = tagRepository;
    this.postTagsService = postTagsService;
  }
  public Page<Tag> get(Pageable page){
    return this.tagRepository.findAll(page);
  }


  public Tag get(Long id){
    return this.tagRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("No tag with id " + id));
  }

  public Tag get(String name){
    return tagRepository.findByNameIgnoreCase(name).orElseThrow(() -> new RessourceNotFoundException("No tag with name " + name));
  }

  public Tag create(String name){
    boolean exist = tagRepository.existsByNameIgnoreCase(name);
    if (exist){
      throw new DataConflictException("Tag name already exists");
    }
    Tag t = new Tag();
    t.setName(name);
    return this.tagRepository.save(t);
  }

  public Tag update(Long id, String name){
    Tag old = this.tagRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("No tag with id " + id));

    boolean existing = this.tagRepository.existsByNameIgnoreCase(name);

    if (existing){
      throw new DataConflictException("Tag name already exists");
    }
    old.setName(name);

    return this.tagRepository.save(old);
  }

  public void delete(Long id){
    this.tagRepository.deleteById(id);
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
