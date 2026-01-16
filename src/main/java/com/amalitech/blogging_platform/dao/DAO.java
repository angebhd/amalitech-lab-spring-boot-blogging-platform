package com.amalitech.blogging_platform.dao;


import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DAO <T, K>{

  T create(T entity);
  T get(K id);

  List<T> getAll(int page, int pageSize);
  T update(K id, T entity);
  boolean delete(K id);
}
