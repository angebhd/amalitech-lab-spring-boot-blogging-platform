-- Script to initialize te DB: Create tables and indexes

-- 1. Users Table
CREATE TABLE "users" (
  "id" BIGSERIAL PRIMARY KEY,
  "username" varchar(12) UNIQUE NOT NULL,
  "first_name" varchar(30),
  "last_name" varchar(30),
  "email" varchar(100) UNIQUE NOT NULL,
  "role" varchar(6) NOT NULL DEFAULT 'USER',
  "password" varchar(255) NOT NULL,
  "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted_at" TIMESTAMP,
  "is_deleted" BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE "tags" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" varchar(20) UNIQUE NOT NULL,
  "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted_at" TIMESTAMP ,
  "is_deleted" BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE "posts" (
  "id" BIGSERIAL PRIMARY KEY,
  "author_id" BIGINT REFERENCES users (id) ON DELETE CASCADE,
  "title" varchar(100) NOT NULL,
  "body" text NOT NULL,
  "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted_at" TIMESTAMP ,
  "is_deleted" BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE "comments" (
  "id" BIGSERIAL PRIMARY KEY,
  "post_id" BIGINT NOT NULL  REFERENCES posts (id) ON DELETE CASCADE,
  "user_id" BIGINT NOT NULL  REFERENCES users (id) ON DELETE CASCADE,
  "body" varchar NOT NULL,
  "parent_comment_id" BIGINT  REFERENCES comments (id) ON DELETE CASCADE,
  "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted_at" TIMESTAMP ,
  "is_deleted" BOOLEAN NOT NULL DEFAULT false
);
CREATE TABLE "reviews" (
  "id" BIGSERIAL PRIMARY KEY,
  "post_id" BIGINT NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
  "user_id" BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  "rate" varchar(5) NOT NULL, -- enum but made varchar for simplicity integration with jpa
  "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted_at" TIMESTAMP ,
  "is_deleted" BOOLEAN NOT NULL DEFAULT false
);
-- Uniq review per post for users
CREATE UNIQUE INDEX IF NOT EXISTS ux_reviews_post_user ON reviews (post_id, user_id) WHERE is_deleted = false;

CREATE TABLE "post_tags" (
  "post_id" BIGINT NOT NULL  REFERENCES posts (id) ON DELETE CASCADE,
  "tag_id" BIGINT NOT NULL  REFERENCES tags (id) ON DELETE CASCADE,
  PRIMARY KEY (post_id, tag_id)
);


-- Indexes for performances
CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_users_is_deleted ON users (is_deleted);
CREATE INDEX IF NOT EXISTS idx_posts_is_deleted_created_at ON posts (is_deleted, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_posts_created_at_desc  ON posts (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_posts_author_id ON posts (author_id);
CREATE INDEX IF NOT EXISTS idx_comments_post_id_created_at ON comments (post_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_post_tags_tag_id ON post_tags (tag_id);

