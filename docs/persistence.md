# Persistence Layer: Spring Data JPA

The Blogging Platform leverages **Spring Data JPA** to abstract database operations, ensuring a clean and maintainable data access layer.

## Repository Abstraction

Repositories extend `JpaRepository`, providing out-of-the-box CRUD operations, pagination, and sorting.

- **`UserRepository`**: Manages user persistence and retrieval.
- **`PostRepository`**: Handles blog post operations, including complex feed queries.
- **`CommentRepository`**: Manages comments associated with posts.
- **`TagRepository`**: Handles post tagging and retrieval by tag name.
- **`ReviewRepository`**: Manages post ratings and reviews.

## Custom Queries

While Spring Data JPA handles standard CRUD via derived query methods (e.g., `findByAuthor_Id`), complex operations use JPQL and Native SQL.

### Native SQL Example: `findAllWithStats`
To retrieve posts with aggregated counts for comments and reviews, a native SQL query is used in `PostRepository`:

```sql
SELECT
  p.id AS id,
  u.username AS authorUsername,
  p.title AS title,
  COUNT(DISTINCT r.id) AS reviews,
  AVG(CASE r.rate WHEN 'ONE' THEN 1 ... END) AS reviewAverage,
  COUNT(DISTINCT c.id) AS comments,
  array_agg(DISTINCT t.name) AS tags
FROM posts p
JOIN users u ON u.id = p.author_id
LEFT JOIN reviews r ON r.post_id = p.id
LEFT JOIN comments c ON c.post_id = p.id
LEFT JOIN post_tags pt ON pt.post_id = p.id
LEFT JOIN tags t ON t.id = pt.tag_id
GROUP BY p.id, u.id
```

## Performance Optimizations

### 1. Fetch Strategies
We use `@EntityGraph` to prevent the N+1 select problem when retrieving posts and their authors:

```java
@EntityGraph(attributePaths = {"author"})
Page<Post> findAll(Pageable page);
```

### 2. Projections
Interface-based projections (`PostWithStatsProjection`) are used to return only the necessary data from complex native queries, reducing memory footprint.

## Transaction Management

The Service Layer uses `@Transactional` to ensure atomicity for multi-step operations (e.g., deleting a post and its associated comments/reviews).

- **Rollback Behavior**: Transactions are automatically rolled back if a `RuntimeException` occurs.
- **Read-Only Transactions**: Optimization for read-heavy methods (optional but recommended).
