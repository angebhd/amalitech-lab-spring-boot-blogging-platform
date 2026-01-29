package com.amalitech.blogging_platform.repository;

import com.amalitech.blogging_platform.model.Post;
import com.amalitech.blogging_platform.model.User;
import com.amalitech.blogging_platform.repository.projections.PostWithStatsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
  @EntityGraph(attributePaths = {"author"})
  Page<Post> findAll(Pageable page);
  Page<Post> findByAuthor_Id(Long id, Pageable pageable);
  void deleteByAuthor(User author);

  @Query(
          value = """
   SELECT
     p.id AS id,

     u.id AS authorId,
     u.username AS authorUsername,
     u.email AS authorEmail,
     u.first_name AS authorFirstName,
     u.last_name AS authorLastName,

     p.title AS title,
     p.body AS body,
     p.created_at AS createdAt,
     p.updated_at AS updatedAt,
     p.deleted_at AS deletedAt,
     p.is_deleted AS isDeleted,

     COUNT(DISTINCT r.id) AS reviews,
     COALESCE(
                AVG(
                  CASE r.rate
                    WHEN 'ONE'   THEN 1
                    WHEN 'TWO'   THEN 2
                    WHEN 'THREE' THEN 3
                    WHEN 'FOUR'  THEN 4
                    WHEN 'FIVE'  THEN 5
                  END
                ),
                0
     ) AS reviewAverage,
     COUNT(DISTINCT c.id) AS comments,

     COALESCE(
       array_agg(DISTINCT t.name)
         FILTER (WHERE t.name IS NOT NULL),
       '{}'
     ) AS tags

   FROM posts p
   JOIN users u ON u.id = p.author_id

   LEFT JOIN reviews r ON r.post_id = p.id
   LEFT JOIN comments c ON c.post_id = p.id
   LEFT JOIN post_tags pt ON pt.post_id = p.id
   LEFT JOIN tags t ON t.id = pt.tag_id

   GROUP BY p.id, u.id
   ORDER BY p.created_at DESC
  """,
          countQuery = """
   SELECT COUNT(*)
   FROM posts p
  """,
          nativeQuery = true
  )
  Page<PostWithStatsProjection> findAllWithStats(Pageable pageable);

  @Query(
          value = """
        SELECT
            p.id AS id,
    
            u.id AS authorId,
            u.username AS authorUsername,
            u.email AS authorEmail,
            u.first_name AS authorFirstName,
            u.last_name AS authorLastName,
    
            p.title AS title,
            p.body AS body,
            p.created_at AS createdAt,
            p.updated_at AS updatedAt,
            p.deleted_at AS deletedAt,
            p.is_deleted AS isDeleted,
    
            COUNT(DISTINCT r.id) AS reviews,
            COALESCE(
                AVG(
                    CASE r.rate
                        WHEN 'ONE' THEN 1
                        WHEN 'TWO' THEN 2
                        WHEN 'THREE' THEN 3
                        WHEN 'FOUR' THEN 4
                        WHEN 'FIVE' THEN 5
                    END
                ), 0
            ) AS reviewAverage,
            COUNT(DISTINCT c.id) AS comments,
    
            COALESCE(
                array_agg(DISTINCT t.name)
                    FILTER (WHERE t.name IS NOT NULL),
                '{}'
            ) AS tags
        
        FROM posts p
        JOIN users u ON u.id = p.author_id
        
        LEFT JOIN reviews r ON r.post_id = p.id
        LEFT JOIN comments c ON c.post_id = p.id
        LEFT JOIN post_tags pt ON pt.post_id = p.id
        LEFT JOIN tags t ON t.id = pt.tag_id
        
        WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(p.body) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.first_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
        
        GROUP BY p.id, u.id
        ORDER BY p.created_at DESC
    """,
          countQuery = """
        SELECT COUNT(*)
        FROM posts p
        JOIN users u ON u.id = p.author_id
        WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(p.body) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.first_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """,
          nativeQuery = true
  )
  Page<PostWithStatsProjection> searchWithStats(@Param("keyword") String keyword, Pageable pageable);

}
