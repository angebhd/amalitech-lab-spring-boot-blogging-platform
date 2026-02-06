# Performance Enhancement: Spring Cache

The Blogging Platform implements **Spring Cache** to reduce database load and improve response times for frequently accessed data.

## Configuration

Caching is enabled via `@EnableCaching` in the main application class. The current implementation uses the default concurrent map-based cache provider (suitable for single-node development).

## Caching Strategy

We target read-heavy operations in the `PostService` to maximize performance gains.

### 1. Cacheable Data (`@Cacheable`)
Individual posts are cached by their ID when retrieved for the first time. Subsequent requests serve the data directly from the cache.

```java
@Cacheable(cacheNames = "posts", key = "#id")
public PostDTO.Out get(Long id) {
    return this.postRepository.findById(id)...
}
```

### 2. Cache Updates (`@CachePut`)
When a post is created or updated, the cache is automatically updated with the latest data to ensure consistency.

```java
@CachePut(cacheNames = "posts", key = "#result.id")
public PostDTO.Out create(PostDTO.In postIn) { ... }

@CachePut(cacheNames = "posts", key = "#id")
public PostDTO.Out update(Long id, PostDTO.In post) { ... }
```

### 3. Cache Eviction (`@CacheEvict`)
When a post is deleted, its entry is removed from the cache to prevent serving stale data.

```java
@CacheEvict(cacheNames = "posts", key = "#id")
public void delete(Long id) { ... }
```

## Performance Impact

- **Read Latency**: Significant reduction (typically <10ms for cached hits vs ~100ms for database queries).
- **Scalability**: Reduces the number of hits on the PostgreSQL database, allowing the system to handle more concurrent readers.

## Strategic Caching Decisions

### Why Not Cache Paginated Elements?
While caching `getFeed` or other paginated results could provide immediate performance boosts, we deliberately avoided this in the final implementation due to several architectural constraints:

1.  **Cache Invalidation Complexity**: Invalidating a specific page when a single post is added, updated, or deleted is non-trivial. A change on page 5 might shift items across all subsequent pages, requiring a full cache eviction of all paginated feed entries to maintain consistency.
2.  **State Management**: Caching results with different `Pageable` parameters (size, page number, sorting) leads to a combinatorial explosion of cache keys, potentially consuming significant memory with redundant data.
3.  **Consistency vs. Performance**: In a dynamic blogging platform, users expect to see new posts or updates immediately in their feed. The overhead of managing consistent paginated caches often outweighs the benefits, especially when optimized JPA queries (like those using `@EntityGraph`) already provide acceptable performance.

Instead, we focus on caching **individual entities** by ID, which offers the best balance of high-speed retrieval and simple, reliable invalidation.
