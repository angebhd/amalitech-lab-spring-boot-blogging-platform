# Persistence Layer: Spring Data JPA

The Blogging Platform leverages **Spring Data JPA** to abstract database operations, ensuring a clean and maintainable data access layer.

## Repository Abstraction

Repositories extend `JpaRepository`, providing out-of-the-box CRUD operations, pagination, and sorting.

- **`UserRepository`**: Manages user persistence, retrieval, and **Role** (ADMIN/USER) mappings.
- **`PostRepository`**: Handles blog post operations, including complex feed queries.
- **`CommentRepository`**: Manages comments associated with posts.
- **`TagRepository`**: Handles post tagging and retrieval by tag name.
- **`ReviewRepository`**: Manages post ratings and reviews.

## Custom Queries

While Spring Data JPA handles standard CRUD via derived query methods (e.g., `findByAuthor_Id`), complex operations use JPQL and Native SQL.

### Native SQL Examples

#### 1. `findAllWithStats`
Retrieves posts with aggregated counts for comments and reviews:

```sql
SELECT p.id, u.username, COUNT(DISTINCT r.id) AS reviews, ...
FROM posts p JOIN users u ON u.id = p.author_id ...
GROUP BY p.id, u.id
```

#### 2. `searchWithStats`
A complex search query that filters posts by title, body, or author attributes (username, first name, last name), while still providing aggregated statistics:

```sql
WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
   OR LOWER(p.body) LIKE LOWER(CONCAT('%', :keyword, '%'))
   OR LOWER(u.first_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
   ...
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

---

## 4. Performance & Caching

The application integrates with **Spring Cache** to optimize read-heavy operations. Detailed analysis can be found in:
- **[Caching Strategy](caching.md)**: Implementation of `@Cacheable` and logic for invalidation.
- **[Performance Report](performance-report.md)**: Side-by-side benchmarks of JPA and Cache optimizations.

## Transaction Management

The Service Layer uses `@Transactional` to ensure atomicity for multi-step operations (e.g., deleting a post and its associated comments/reviews).

- **Rollback Behavior**: Transactions are automatically rolled back if a `RuntimeException` occurs.
- **Read-Only Transactions**: Optimization for read-heavy methods (optional but recommended).

---

## Database Migrations with Flyway

While **Spring Data JPA** is excellent for abstracting CRUD operations, it is **not recommended for managing database schema migrations** in production environments. Instead, we use **Flyway** for version-controlled, reliable schema management.

### Why Flyway?
- **Version Control**: Each migration is stored as a versioned SQL script, enabling full audit trails.
- **Consistency**: Ensures all environments (dev, test, prod) have identical schemas.
- **Safety**: Flyway validates migration checksums to prevent unauthorized changes.
- **Simplicity**: Migrations run automatically on application startup.

### Migration Files
Migrations are stored in `src/main/resources/db/migration/` and follow the naming convention `V{version}__{description}.sql`:

- **`V001__init_tables.sql`**: Initial schema creation (users, posts, comments, tags, reviews, and relationships).
- **`V002__feed_database.sql`**: Sample data population for testing and development.

### Best Practices
1. **Never modify existing migration files** after they've been applied. Create a new migration instead.
2. **Test migrations locally** before deploying to production.
3. **Use descriptive names** for migration files to clarify intent.
4. **Disable JPA auto-DDL** in production (`spring.jpa.hibernate.ddl-auto=validate`) to prevent unintended schema changes.
