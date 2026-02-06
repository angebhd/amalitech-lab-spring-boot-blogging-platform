# GraphQL Integration

The blogging platform exposes a comprehensive GraphQL API alongside the REST endpoints, allowing clients to request exactly the data they need.

## Schema Overview

The GraphQL schema is modularized by domain and located in `src/main/resources/graphql`.

### Core Types & Fields

#### User (`users.graphqls`)
Represents a registered user on the platform.
```graphql
type User {
    id: ID
    username: String
    firstName: String
    lastName: String
    email: String
    createdAt: String
    # ... other fields
}
```

#### Post (`posts.graphqls`)
Represents a blog post.
```graphql
type Post {
    id: ID
    author: User
    reviews: [Review]
    comments: [Comment]
    Tags: [Tag]
    title: String
    body: String
    createdAt: String
    # ... other fields
}
```

#### Comment (`comments.graphqls`) & Tag (`tags.graphqls`)
Standard entities supporting the blog ecosystem.

---

## Operations

### Queries
Retrieve data from the server. All list operations support pagination (`page`, `size`).

#### Posts
- `posts(page: Int, size: Int): PaginatedPost` - Get all posts.
- `postById(id: Int): Post` - Get a single post by ID.
- `postByAuthorId(page: Int, size: Int, id: Int): PaginatedPost` - Get posts by a specific author.
- `postSearch(page: Int, size: Int, keyword: String, tagId: Int): PaginatedPost` - Search posts by keyword or tag.

#### Users
- `users(page: Int, size: Int): UserPaginated` - List all users.
- `userById(id: Int): User` - Get user by ID.
- `userByUsername(username: String): User` - Get user by username.

#### Comments & Tags
- `comments(page: Int, size: Int): PaginatedComment`
- `tags(page: Int, size: Int): TagPaginated`

### Mutations
Modify data on the server.

#### Posts
- `createPost(input: PostInput!): Post`
- `updatePost(id: ID!, input: PostInput!): Post`
- `deletePost(id: ID!): Post`

#### Users
- `createUser(input: UserInput!): User`
- `updateUser(id: ID!, input: UserInput!): User`
- `deleteUser(id: ID!): User`

#### Comments & Tags
- `createComment`, `updateComment`, `deleteComment`
- `createTag`, `updateTag`, `deleteTag`

---

## Examples

### 1. Fetching a Detailed Post
Get a post with its author's name and all associated tags in a single request.

```graphql
query GetPostDetails {
  postById(id: 1) {
    title
    body
    createdAt
    author {
      username
      email
    }
    Tags {
      name
    }
  }
}
```

### 2. Creating a New Post
Create a post and immediately get back the ID and creation timestamp.

```graphql
mutation CreateNewPost {
  createPost(input: {
    authorId: "1",
    title: "My First Blog Post",
    body: "This is the content of my post...",
    tags: ["tech", "spring-boot"]
  }) {
    id
    createdAt
    title
  }
}
```

### 3. Searching Posts
Search for posts containing "Spring" and request pagination info.

```graphql
query SearchPosts {
  postSearch(keyword: "Spring", page: 1, size: 5) {
    total
    totalPages
    items {
      id
      title
      author {
        username
      }
    }
  }
}
```

---

## Coexistence with REST API

This application uses a hybrid approach:
- **REST API**: Best for standard CRUD operations and simple integrations.
- **GraphQL**: Ideal for complex views requiring nested data (e.g., a Post with its Author and top Comments) in a single request, reducing over-fetching and under-fetching.

## Tools

- **GraphiQL**: An in-browser IDE for exploring GraphQL.
  - URL: `http://localhost:8080/graphiql`
