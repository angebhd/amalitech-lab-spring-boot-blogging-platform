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

> [!TIP]
> Future enhancements could include integrating **Redis** as a distributed cache provider for multi-node deployments.
