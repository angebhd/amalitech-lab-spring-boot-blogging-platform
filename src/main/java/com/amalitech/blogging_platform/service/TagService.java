package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.TagDTO;
import com.amalitech.blogging_platform.exceptions.DataConflictException;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.Tag;
import com.amalitech.blogging_platform.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TagService {

  private final TagRepository tagRepository;

  @Autowired
  public TagService(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }
  public PaginatedData<TagDTO.Out> get(Pageable page){
    return new PaginatedData<>(this.tagRepository.findAll(page).map(TagDTO.Converter::toDTO));
  }

  List<Tag> getOrCreateTags(List<String> tagNames) {

    if (tagNames == null || tagNames.isEmpty())
      return List.of();

    // Return the list of tags already in the DB
    List<Tag> existingTags = tagRepository.findAllByNameInIgnoreCase(tagNames);

    // Put the existing tag in the map for verification tin the next block
    Map<String, Tag> existingMap = existingTags
            .stream()
            .collect(Collectors.toMap(t -> t.getName().toLowerCase(), t -> t));

    List<Tag> result = new ArrayList<>();

    //Looping through the TagNames to see if they were found, if they were not, create them
    List<Tag> tagToSave = new ArrayList<>();
    for (String name : tagNames) {
      String key = name.toLowerCase();
      if (existingMap.containsKey(key)) {
        result.add(existingMap.get(key));
      } else {
        Tag newTag = new Tag();
        newTag.setName(name);
        tagToSave.add(newTag);
      }
    }
    // batch save & Add to result
    if(!tagToSave.isEmpty()){
      result.addAll(tagRepository.saveAll(tagToSave));
    }

    return result;
  }

  @Cacheable(cacheNames = "tags", key="#id")
  public TagDTO.Out get(Long id){
    return this.tagRepository.findById(id).map(TagDTO.Converter::toDTO).orElseThrow(() -> new RessourceNotFoundException("No tag with id " + id));
  }
  @Cacheable(cacheNames = "tagsByName", key = "#name.toLowerCase()" )
  public TagDTO.Out get(String name){
    return tagRepository.findByNameIgnoreCase(name).map(TagDTO.Converter::toDTO).orElseThrow(() -> new RessourceNotFoundException("No tag with name " + name));
  }

  @Caching(
          put = {
                  @CachePut(cacheNames = "tags", key = "#result.id"),
                  @CachePut(cacheNames = "tagsByName", key = "#result.name.toLowerCase()")
          }
  )
  public TagDTO.Out create(String name){
    boolean exist = tagRepository.existsByNameIgnoreCase(name);
    if (exist){
      throw new DataConflictException("Tag name already exists");
    }
    Tag t = new Tag();
    t.setName(name);
    return TagDTO.Converter.toDTO(this.tagRepository.save(t));
  }

  @Caching(
          put = {
                  @CachePut(cacheNames = "tags", key = "#id"),
                  @CachePut(cacheNames = "tagsByName", key = "#name.toLowerCase()")
          },
          evict = {
                  @CacheEvict(cacheNames = "tagsByName", allEntries = true)
          }
  )
  public TagDTO.Out update(Long id, String name){
    Tag old = this.tagRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("No tag with id " + id));

    boolean existing = this.tagRepository.existsByNameIgnoreCase(name);

    if (existing){
      throw new DataConflictException("Tag name already exists");
    }
    old.setName(name);

    return TagDTO.Converter.toDTO(this.tagRepository.save(old));
  }

  @Caching(evict = {
          @CacheEvict(cacheNames = "tags", key = "#tagId"),
          @CacheEvict(cacheNames = "tagsByName", key = "#tagName")
  })
  public void delete(Long id){
    this.tagRepository.deleteById(id);
  }

}
