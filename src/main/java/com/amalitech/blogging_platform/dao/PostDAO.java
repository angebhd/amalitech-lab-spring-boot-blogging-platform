package com.amalitech.blogging_platform.dao;


import com.amalitech.blogging_platform.dto.CommentDTO;
import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.PostDTO;
import com.amalitech.blogging_platform.model.Post;
import com.amalitech.blogging_platform.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data Access Object (DAO) for Post entities.
 * Provides CRUD operations for blog posts with soft-delete support.
 * <p>
 * All read operations exclude soft-deleted records by default,
 * but provide overloads to include them when needed (e.g. admin views, audit, recovery).
 * </p>
 */
@Repository
public class PostDAO implements DAO<Post, Long> {

  private final Logger log = LoggerFactory.getLogger(PostDAO.class);

  /**
   * Creates a new post in the database and sets the generated ID and timestamps on the entity.
   *
   * @param entity the post to create (will be modified to include generated ID and timestamps)
   * @return the same entity instance with generated fields populated
   * @throws RuntimeException if a database error occurs during insertion
   */
  @Override
  public Post create(Post entity) {
    final String INSERT = """
                INSERT INTO posts (author_id, title, body)
                VALUES (?, ?, ?)
                RETURNING id, created_at, updated_at
            """;

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(INSERT)) {

      ps.setLong(1, entity.getAuthorId());
      ps.setString(2, entity.getTitle());
      ps.setString(3, entity.getBody());

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          entity.setId(rs.getLong("id"));
          entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
          entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
      }

      log.info("Post created successfully - ID: {}, Title: {}", entity.getId(), entity.getTitle());
      return entity;

    } catch (SQLException e) {
      log.error("Error creating post", e);
      throw new RuntimeException("Failed to create post", e);
    }
  }

  /**
   * Retrieves a post by its primary key (ID), excluding soft-deleted records by default.
   *
   * @param id the unique identifier of the post
   * @return the matching post or {@code null} if not found or soft-deleted
   * @throws RuntimeException if a database error occurs
   * @see #get(Long, boolean)
   */
  @Override
  public Post get(Long id) {
    return get(id, false);
  }

  /**
   * Retrieves a post by its primary key (ID), with optional inclusion of soft-deleted records.
   *
   * @param id             the unique identifier of the post
   * @param includeDeleted if {@code true}, returns the post even if marked as deleted
   * @return the matching post or {@code null} if not found
   * @throws RuntimeException if a database error occurs
   */
  public Post get(Long id, boolean includeDeleted) {
    String sql = """
                SELECT id, author_id, title, body, created_at, updated_at, is_deleted
                FROM posts
                WHERE id = ?
            """;

    if (!includeDeleted) {
      sql += " AND is_deleted = false";
    }

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql)) {

      ps.setLong(1, id);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return mapRowToPost(rs);
        }
      }

    } catch (SQLException e) {
      log.error("Error fetching post with id {}", id, e);
      throw new RuntimeException("Failed to fetch post by id", e);
    }

    return null;
  }


  /**
   * Retrieves a paginated list of posts, excluding soft-deleted records by default.
   *
   * @param page     1-based page number
   * @param pageSize number of records per page
   * @return paginated data of posts
   * @throws RuntimeException if a database error occurs
   * @see #getAll(int, int, boolean)
   */
  @Override
  public PaginatedData<Post> getAll(int page, int pageSize) {
    return getAll(page, pageSize, false);
  }

  /**
   * Retrieves a paginated list of posts with optional inclusion of soft-deleted records.
   *
   * @param page           1-based page number
   * @param pageSize       number of records per page
   * @param includeDeleted if {@code true}, includes soft-deleted posts
   * @return paginated data of posts
   * @throws RuntimeException if a database error occurs
   */
  public PaginatedData<Post> getAll(int page, int pageSize, boolean includeDeleted) {
    int effectivePage = Math.max(page, 1);
    int effectivePageSize = Math.max(pageSize, 1);
    int offset = (effectivePage - 1) * effectivePageSize;

    String countSql = "SELECT COUNT(*) FROM posts";
    if (!includeDeleted) {
      countSql += " WHERE is_deleted = false";
    }

    String dataSql = """
                SELECT id, author_id, title, body, created_at, updated_at, is_deleted
                FROM posts
            """;

    if (!includeDeleted) {
      dataSql += " WHERE is_deleted = false";
    }

    dataSql += """
                 ORDER BY created_at DESC
                 LIMIT ? OFFSET ?
            """;

    List<Post> posts = new ArrayList<>();
    int total = 0;

    try (Connection connection = DatabaseConnection.getConnection()) {

      // Fetch total count
      try (PreparedStatement countPs = connection.prepareStatement(countSql);
           ResultSet countRs = countPs.executeQuery()) {
        if (countRs.next()) {
          total = countRs.getInt(1);
        }
      }

      // Fetch paginated data
      try (PreparedStatement ps = connection.prepareStatement(dataSql)) {
        ps.setInt(1, effectivePageSize);
        ps.setInt(2, offset);

        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            posts.add(mapRowToPost(rs));
          }
        }
      }

    } catch (SQLException e) {
      log.error("Error fetching paginated posts (page={}, size={}, includeDeleted={})",
              effectivePage, effectivePageSize, includeDeleted, e);
      throw new RuntimeException("Failed to fetch posts", e);
    }

    int totalPages = (total + effectivePageSize - 1) / effectivePageSize;
    return new PaginatedData<>(posts, effectivePage, effectivePageSize, totalPages, total);
  }

  /**
   * Retrieves a paginated list of posts for an author
   *
   * @param authorId        id of the author
   * @return  list of posts
   * @throws RuntimeException if a database error occurs
   */
  public PaginatedData<Post> getByAuthorId(Long authorId, int page, int pageSize) {

    int effectivePage = Math.max(page, 1);
    int effectivePageSize = Math.max(pageSize, 1);
    int offset = (effectivePage - 1) * effectivePageSize;

    String countSql = """
                SELECT COUNT(*) FROM posts WHERE author_id = ? AND is_deleted = false
            """;

    String dataSql = """
                SELECT id, author_id, title, body, created_at, updated_at, is_deleted
                FROM posts
                WHERE author_id = ? AND is_deleted = false
                ORDER BY created_at DESC
                LIMIT ? OFFSET ?
            """;

    List<Post> posts = new ArrayList<>();
    int total = 0;

    try (Connection connection = DatabaseConnection.getConnection()) {

      // Fetch total count
      try (PreparedStatement countPs = connection.prepareStatement(countSql)) {
        countPs.setLong(1, authorId);
        try (ResultSet countRs = countPs.executeQuery()) {
          if (countRs.next()) {
            total = countRs.getInt(1);
          }
        }
      }

      // Fetch paginated data
      try (PreparedStatement ps = connection.prepareStatement(dataSql)) {
        ps.setLong(1, authorId);
        ps.setInt(2, effectivePageSize);
        ps.setInt(3, offset);

        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            posts.add(mapRowToPost(rs));
          }
        }
      }

    } catch (SQLException e) {
      log.error("Error fetching by author with id: {}", authorId, e);
      throw new RuntimeException("Failed to fetch posts", e);
    }

    int totalPages = (total + effectivePageSize - 1) / effectivePageSize;
    return new PaginatedData<>(posts, effectivePage, effectivePageSize, totalPages, total);
  }

  /**
   * Convenience method: first page (1), 100 records, excludes deleted posts.
   *
   * @return paginated data of up to 100 most recently created non-deleted posts
   */
  public PaginatedData<Post> getAll() {
    return getAll(1, 100, false);
  }

  /**
   * Updates an existing post's title and body.
   * Automatically updates the updated_at timestamp.
   * Author cannot be changed via this method.
   *
   * @param id     ID of the post to update
   * @param entity updated post data
   * @return updated entity or {@code null} if not found or deleted
   * @throws RuntimeException if a database error occurs
   */
  @Override
  public Post update(Long id, Post entity) {
    final String UPDATE = """
                UPDATE posts
                SET title = ?,
                    body = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ? AND is_deleted = false
                RETURNING updated_at
            """;

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(UPDATE)) {

      ps.setString(1, entity.getTitle());
      ps.setString(2, entity.getBody());
      ps.setLong(3, id);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          entity.setId(id);
          entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
          log.info("Post updated successfully - ID: {}", id);
          return entity;
        }
      }

      log.warn("No post found to update with id {}", id);
      return null;

    } catch (SQLException e) {
      log.error("Error updating post with id {}", id, e);
      throw new RuntimeException("Failed to update post", e);
    }
  }

  /**
   * Soft-deletes a post by setting is_deleted = true and recording deletion timestamp.
   *
   * @param id ID of the post to soft-delete
   * @return {@code true} if the post was found and marked deleted, {@code false} otherwise
   * @throws RuntimeException if a database error occurs
   */
  @Override
  public boolean delete(Long id) {
    final String DELETE = """
                UPDATE posts
                SET is_deleted = true,
                    deleted_at = CURRENT_TIMESTAMP
                WHERE id = ? AND is_deleted = false
            """;

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(DELETE)) {

      ps.setLong(1, id);

      boolean deleted = ps.executeUpdate() > 0;

      if (deleted) {
        log.info("Post soft-deleted successfully - ID: {}", id);
      } else {
        log.warn("Post not found or already deleted - ID: {}", id);
      }

      return deleted;

    } catch (SQLException e) {
      log.error("Error soft-deleting post with id {}", id, e);
      throw new RuntimeException("Failed to delete post", e);
    }
  }


  public PostDTO.Detailed getPostDTO(Long postId, boolean includeDeleted) {
    String sql = """
            SELECT 
                p.id, p.author_id, p.title, p.body,
                p.created_at, p.updated_at, p.is_deleted,
                u.username AS author_username,
                COALESCE(NULLIF(u.first_name || ' ' || u.last_name, ' '), u.username) AS author_name
            FROM posts p
            LEFT JOIN users u ON p.author_id = u.id
            WHERE p.id = ?
              AND (p.is_deleted = false OR ? = true)
            """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setLong(1, postId);
      ps.setBoolean(2, includeDeleted);

      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }

        Post post = mapRowToPost(rs);
        post.setDeleted(rs.getBoolean("is_deleted"));

        PostDTO.Detailed dto = new PostDTO.Detailed();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setBody(post.getBody());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setDeletedAt(null); // Assuming deletedAt not fetched
        dto.setDeleted(post.isDeleted());
        dto.setAuthorName(rs.getString("author_name"));

        // Load supporting data
        List<Tag> tagsList = getTagsForPost(postId);
        Set<String> tags = tagsList.stream().map(Tag::getName).collect(Collectors.toSet());
        dto.setTags(tags);
        dto.setReviews(new ArrayList<>());
        List<CommentDTO.Out> comments = getCommentDTOsForPost(postId);
        dto.setComments(comments);

        return dto;
      }

    } catch (SQLException e) {
      log.error("Failed to load PostDTO id={}", postId, e);
      throw new RuntimeException("Database error fetching single post", e);
    }
  }





  public PaginatedData<PostDTO.Detailed> getPostDTOs(
          int page,
          int pageSize,
          String search,
          Long tagId,
          Long authorId,
          boolean includeDeleted) {

    int effectivePage = Math.max(1, page);
    int effectiveSize = Math.max(1, Math.min(pageSize, 50));
    int offset = (effectivePage - 1) * effectiveSize;

    StringBuilder baseSql = new StringBuilder("""
            FROM posts p
            LEFT JOIN users u ON p.author_id = u.id
            """);

    List<Object> params = new ArrayList<>();
    String and = " WHERE ";

    if (!includeDeleted) {
      baseSql.append(and).append("p.is_deleted = false ");
      and = "AND ";
    }

    if (authorId != null) {
      baseSql.append(and).append("p.author_id = ? ");
      params.add(authorId);
      and = "AND ";
    }

    if (tagId != null) {
      baseSql.append(and).append("""
                EXISTS (SELECT 1 FROM post_tags pt WHERE pt.post_id = p.id AND pt.tag_id = ?)
                """);
      params.add(tagId);
      and = "AND ";
    }

    if (search != null && !search.trim().isEmpty()) {
      String term = "%" + search.trim().toLowerCase() + "%";
      baseSql.append(and).append("""
                (LOWER(p.title) LIKE ? OR LOWER(p.body) LIKE ? OR LOWER(u.username) LIKE ? OR LOWER(u.first_name) LIKE ? OR LOWER(u.last_name) LIKE ?)
                """);
      params.add(term);
      params.add(term);
      params.add(term);
      params.add(term);
      params.add(term);
      and = "AND ";
    }

    String countSql = "SELECT COUNT(DISTINCT p.id) " + baseSql.toString();

    String dataSql = """
            SELECT DISTINCT
                p.id, p.author_id, p.title, p.body,
                p.created_at, p.updated_at, p.is_deleted,
                u.username AS author_username,
                COALESCE(NULLIF(u.first_name || ' ' || u.last_name, ' '), u.username) AS author_name
            """ + baseSql.toString() + """
             ORDER BY p.created_at DESC
             LIMIT ? OFFSET ?
            """;
    params.add(effectiveSize);
    params.add(offset);

    List<PostDTO.Detailed> dtos = new ArrayList<>();
    int total = 0;

    try (Connection conn = DatabaseConnection.getConnection()) {

      // Fetch total count
      try (PreparedStatement countPs = conn.prepareStatement(countSql)) {
        for (int i = 0; i < params.size() - 2; i++) { // Exclude LIMIT and OFFSET
          countPs.setObject(i + 1, params.get(i));
        }
        try (ResultSet countRs = countPs.executeQuery()) {
          if (countRs.next()) {
            total = countRs.getInt(1);
          }
        }
      }

      // Fetch data
      try (PreparedStatement ps = conn.prepareStatement(dataSql)) {

        for (int i = 0; i < params.size(); i++) {
          ps.setObject(i + 1, params.get(i));
        }

        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            Post post = mapRowToPost(rs);
            post.setDeleted(rs.getBoolean("is_deleted"));

            PostDTO.Detailed dto = new PostDTO.Detailed();
            dto.setId(post.getId());
            dto.setTitle(post.getTitle());
            dto.setBody(post.getBody());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setUpdatedAt(post.getUpdatedAt());
            dto.setDeletedAt(null); // Assuming deletedAt not fetched
            dto.setDeleted(post.isDeleted());
            dto.setAuthorName(rs.getString("author_name"));

            // Only tags — no comments on list view
            List<Tag> tagsList = getTagsForPost(post.getId());
            Set<String> tags = tagsList.stream().map(Tag::getName).collect(Collectors.toSet());
            dto.setTags(tags);
            dto.setComments(new ArrayList<>());
            dto.setReviews(new ArrayList<>());

            dtos.add(dto);
          }
        }
      }

    } catch (SQLException e) {
      log.error("Failed to load paginated PostDTOs", e);
      throw new RuntimeException("Error fetching post list", e);
    }

    int totalPages = (total + effectiveSize - 1) / effectiveSize;
    return new PaginatedData<>(dtos, effectivePage, effectiveSize, totalPages, total);
  }




  /**
   * Maps a ResultSet row to a Post object.
   *
   * @param rs the result set positioned at the current row
   * @return populated Post instance
   * @throws SQLException if column access fails
   */
  private Post mapRowToPost(ResultSet rs) throws SQLException {
    Post post = new Post();
    post.setId(rs.getLong("id"));
    post.setAuthorId(rs.getLong("author_id"));
    post.setTitle(rs.getString("title"));
    post.setBody(rs.getString("body"));
    post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
    post.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
    post.setDeleted(rs.getBoolean("is_deleted"));
    return post;
  }

  private List<Tag> getTagsForPost(Long postId) {
    String sql = """
            SELECT t.id, t.name
            FROM tags t
            INNER JOIN post_tags pt ON t.id = pt.tag_id
            WHERE pt.post_id = ?
              AND t.is_deleted = false
            ORDER BY t.name
            """;

    List<Tag> tags = new ArrayList<>();

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setLong(1, postId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          Tag tag = new Tag();
          tag.setId(rs.getLong("id"));
          tag.setName(rs.getString("name"));
          tags.add(tag);
        }
      }
    } catch (SQLException e) {
      log.warn("Could not load tags for post {}", postId, e);
    }
    return tags;
  }

  private List<CommentDTO.Out> getCommentDTOsForPost(Long postId) {
    String sql = """
            SELECT 
                c.id, c.user_id, c.body, c.parent_comment, c.created_at,
                u.username AS commenter_username,
                u.first_name || ' ' || u.last_name AS commenter_fullname
            FROM comments c
            LEFT JOIN users u ON c.user_id = u.id
            WHERE c.post_id = ?
              AND c.is_deleted = false
            ORDER BY c.created_at ASC
            """;

    List<CommentDTO.Out> comments = new ArrayList<>();

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setLong(1, postId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          CommentDTO.Out dto = new CommentDTO.Out();
          dto.setId(rs.getLong("id"));
          dto.setPostId(postId);
          dto.setUserId(rs.getLong("user_id"));
          dto.setBody(rs.getString("body"));
          Long parentId = rs.getLong("parent_comment");
          if (!rs.wasNull()) {
            dto.setParentCommentId(parentId);
          }
          dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
          dto.setUpdatedAt(null); // Not fetched
          dto.setDeletedAt(null); // Not fetched
          dto.setDeleted(false);
          // Commenter name not set as no field in DTO
          comments.add(dto);
        }
      }

    } catch (SQLException e) {
      log.error("Failed to load comments for post {}", postId, e);
    }

    return comments;
  }


}