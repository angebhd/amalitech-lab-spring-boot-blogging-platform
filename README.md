# Blogging Platform

A web-based blogging platform built with **Spring Boot** and **PostgreSQL**. The application transforms a standard backend into a layered system suitable for enterprise-grade development, featuring **RESTful** and **GraphQL APIs**, comprehensive **validation**, **centralized exception handling**, and **AOP-based monitoring**.

---

## System Architecture

The platform follows a layered architecture to ensure separation of concerns and maintainability:

- **Presentation Layer**: Exposes data via **REST controllers** and **GraphQL resolvers**.
- **Service Layer**: Implements core business logic, including **Argon2-based password hashing**, **Spring Cache** abstraction, and **Transaction Management**.
- **Data Access Layer**: Handles persistence using **Spring Data JPA** with **PostgreSQL**, utilizing repository abstraction and custom JPQL/Native queries.
- **Cross-Cutting Concerns**: Uses **Spring AOP** for logging, performance monitoring, and centralized error handling via `@ControllerAdvice`.
- **Domain Model**: Defines core entities (User, Post, Comment, Tag, Review) with JPA annotations.

---

## Architectural Concepts

This project implements several key software design patterns to ensure scalability, maintainability, and code quality.

### 1. Inversion of Control (IoC) & Dependency Injection (DI)
Spring's IoC container manages the lifecycle and configuration of application objects (Beans). This project strictly adheres to **Constructor-Based Dependency Injection**.
- **Implementation**: Controllers (e.g., `PostController`) declare their dependencies (e.g., `PostService`) as `final` fields and receive them via the constructor.
- **Advantages**:
  - **Immutability**: Dependencies cannot be changed after object instantiation, ensuring thread safety.
  - **Testability**: Classes can be easily instantiated with mock dependencies in unit tests without relying on the Spring Context or reflection.
  - **Null Safety**: Dependencies are guaranteed to be non-null when the bean is created.

### 2. Model-View-Controller (MVC) Pattern
The application follows the classic MVC separation of concerns:
- **Controller (View/Interface)**: Handles incoming HTTP requests, validates input using DTOs, and invokes business logic.
- **Service (Model Logic)**: Contains the core business rules, transaction boundaries (`@Transactional`), and caching logic. It orchestrates calls to the Data Access Layer.
- **Repository (Model Data)**: The Spring Data JPA layer provides an abstraction over the PostgreSQL database, automating CRUD operations and supporting complex query derivation.

### 3. Aspect-Oriented Programming (AOP)
Cross-cutting concerns are modularized using Spring AOP, preventing code duplication in business methods.
- **Logging**: The `LoggingAspect` automatically logs method entry, exit, arguments, and exceptions for all service methods.
- **Monitoring**: The `PerformanceAspect` tracks execution metrics, providing insights into method performance and aid in optimization.

### 4. GraphQL Overview
In addition to REST, the platform offers a GraphQL API to provide clients with flexible data fetching capabilities.
- **Advantages**:
  - **Precise Data Fetching**: Clients specify exactly what data they need, preventing over-fetching (receiving unused data) and under-fetching (needing multiple requests).
  - **Single Round-Trip**: Complex hierarchical data (e.g., a Post with its Author and Comments) can be retrieved in a single network request.
- **Disadvantages**:
  - **Complexity**: Requires defining strict schemas and resolvers, which can be more complex than standard REST endpoints.
  - **Caching**: Standard HTTP caching is less effective since all requests typically use POST against a single endpoint.

---

## Features

- **Hybrid API Support**:
  - **RESTful Endpoints**: Standardized CRUD operations for all resources.
  - **GraphQL Integration**: Flexible data fetching for complex requirements. [Read more](docs/graphql.md).
- **Security**: 
  - **Argon2 Hashing**: Industry-standard password hashing for user security.
- **Persistence & Performance**:
  - **Spring Data JPA**: Abstraction for cleaner data access code. [Read more](docs/persistence.md).
  - **Spring Cache**: Improving read performance for popular posts and users. [Read more](docs/caching.md).
  - **Transaction Management**: Ensuring data consistency with `@Transactional`.
  - **Database-Level Pagination**: Efficient data retrieval using `Pageable`.
- **Quality & Monitoring**:
  - **Validation**: Strict input validation using Bean Validation.
  - **AOP Monitoring**: Automated logging and performance tracking. [Read more](docs/aop.md).
  - **Performance Benchmarking**: Detailed analysis of query and cache optimizations. [Read more](docs/performance-report.md).
  - **OpenAPI Documentation**: Interactive API testing with Swagger UI.

---

## Project Structure

```text
.
├── docs/                             # Documentation & SQL scripts
│   ├── aop.md                       # AOP Implementation details
│   ├── caching.md                   # Spring Cache strategy
│   ├── database-design.md           # Conceptual, Logical, & Physical models
│   ├── graphql.md                   # GraphQL Integration details
│   ├── performance-report.md        # Benchmarking & Optimization analysis
│   ├── persistence.md               # Spring Data JPA & Repository details
│   ├── script.sql                   # Schema creation script
│   └── feedDB.sql                   # Sample data script
├── src/main/java/com/amalitech/blogging_platform/
│   ├── BloggingPlatformApplication.java # Spring Boot Entry Point
│   ├── aspect/                      # AOP Aspects (Logging, Performance)
│   ├── config/                      # Configuration (OpenAPI, Cache, etc.)
│   ├── controller/                  # REST Controllers & GraphQL Resolvers
│   ├── dto/                         # Data Transfer Objects
│   ├── exceptions/                  # Global Exception Handlers
│   ├── model/                       # JPA Entity Definitions
│   ├── repository/                  # Spring Data Repositories
│   └── service/                     # Business Logic & Transactions
├── src/main/resources/              # Assets & Configuration
│   ├── graphql/                     # GraphQL Schemas (.graphqls)
│   ├── application.yaml             # Main Configuration (uses .env)
│   └── application-dev.yaml         # Dev Profile
└── pom.xml                          # Maven Dependencies
```

---

## Tech Stack & Dependencies

### Core Technologies
- **Framework**: Spring Boot 3.x (Spring Data JPA, Spring Cache)
- **Language**: Java 21
- **Database**: PostgreSQL 16+
- **API**: REST (OpenAPI) & GraphQL
- **Security**: Argon2 JVM

### Key Dependencies
- **Spring Web / GraphQL / Validation / AOP**: Core Spring components.
- **Argon2 JVM**: For secure password hashing.
- **Dotenv-Java**: For environment variable management.
- **Lombok**: Boilerplate reduction.
- **Springdoc OpenApi**: Automated Swagger UI generation.

---

---

## Configuration & Profiles

The application uses **Spring Profiles** to manage environment-specific configurations, particularly for API documentation tools.

### Profiles
- **`dev`** (Default): Designed for local development.
  - **Enabled**: Swagger UI (`/swagger-ui.html`) and GraphiQL (`/graphiql`).
- **`prod`**: Designed for production deployment.
  - **Disabled**: Swagger UI and GraphiQL are disabled to prevent exposing API structure publicly.

To run with a specific profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## Setup & Installation

### 1. Prerequisites
- JDK 21
- PostgreSQL Installed and Running
- Maven 3.8+

### 2. Configuration
1. Clone the repository and navigate to the root directory.
2. Create your environment file:
   ```bash
   cp .env.example .env
   ```
3. Update `.env` with your PostgreSQL credentials:
   ```properties
   DB_URL=jdbc:postgresql://localhost:5432/blogging
   DB_USER=your_username
   DB_PASSWORD=your_password
   ```

### 3. Database Initialization
1. Create a database named `blogging`.
2. Run the schema and seed scripts:
   ```bash
   psql -d blogging -f docs/script.sql
   psql -d blogging -f docs/feedDB.sql
   ```

### 4. Running the App
```bash
./mvnw spring-boot:run
```

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **GraphiQL**: [http://localhost:8080/graphiql](http://localhost:8080/graphiql)

---

## Testing Caching & Performance

To verify the performance enhancements and caching behavior:

1. **Initial Retrieval**: Perform a `GET` request to `/api/posts/{id}`. Note the response time in your API client (e.g., Postman). This triggers a database hit and populates the cache.
2. **Cached Retrieval**: Perform the same `GET` request. The response time should drop significantly (e.g., from ~100ms to <20ms) as data is served from memory.
3. **Cache Invalidation**: Perform a `PUT` or `DELETE` request on the same post.
4. **Verification**: Perform the `GET` request again. For `PUT`, you should see the updated data immediately (Cache Update). For `DELETE`, the first subsequent `GET` will return a `404`, and any database logs will show the entry was evicted.
5. **N+1 Verification**: Check the application console/logs while fetching the feed. With `@EntityGraph` enabled, you should see simplified SQL joins instead of multiple recursive queries for authors or comments.