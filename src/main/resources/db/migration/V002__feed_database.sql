INSERT INTO users (username, first_name, last_name, email, role, password)
VALUES
('admin01', 'Alice', 'Admin', 'alice.admin@example.com', 'ADMIN', '$2a$10$adminhash'),
('john_d', 'John', 'Doe', 'john.doe@example.com', 'USER', '$2a$10$userhash1'),
('jane_s', 'Jane', 'Smith', 'jane.smith@example.com', 'USER', '$2a$10$userhash2'),
('mark_t', 'Mark', 'Taylor', 'mark.taylor@example.com', 'USER', '$2a$10$userhash3');

INSERT INTO tags (name)
VALUES
('JAVA'),
('SPRING'),
('POSTGRES'),
('BACKEND'),
('TUTORIAL');

INSERT INTO posts (author_id, title, body)
VALUES
(1, 'Welcome to the Blog', 'This is the first admin post welcoming users to the platform.'),
(2, 'Spring Boot Basics', 'An introduction to Spring Boot and how to get started quickly.'),
(3, 'PostgreSQL Tips', 'Useful PostgreSQL tips for better performance and indexing.'),
(4, 'Building REST APIs', 'A practical guide to building REST APIs with Java.');


INSERT INTO post_tags (post_id, tag_id)
VALUES
(1, 4), -- backend
(2, 1), -- java
(2, 2), -- spring
(2, 5), -- tutorial
(3, 3), -- postgres
(3, 4), -- backend
(4, 1), -- java
(4, 4); -- backend

INSERT INTO comments (post_id, user_id, body)
VALUES
(2, 3, 'Great introduction, very helpful!'),
(2, 4, 'I like how concise this guide is.'),
(3, 2, 'Indexes really made a difference for me.');

-- Replies
INSERT INTO comments (post_id, user_id, body, parent_comment_id)
VALUES
(2, 2, 'Glad you found it useful!', 1),
(3, 1, 'Totally agree, indexing is critical.', 3);

INSERT INTO reviews (post_id, user_id, rate)
VALUES
(2, 3, 'FIVE'),
(2, 4, 'FOUR'),
(3, 2, 'FOUR'),
(4, 3, 'THREE');

