# Blogging Platform

A web-based blogging platform built with **Spring Boot** and **PostgreSQL**. The application transforms a standard backend into a layered system suitable for enterprise-grade development, featuring **RESTful** and **GraphQL APIs**, comprehensive **validation**, **centralized exception handling**, and **AOP-based monitoring**.

---

## System Architecture

The platform follows a layered architecture to ensure separation of concerns and maintainability:

- **Presentation Layer**: Exposes data via **REST controllers** and **GraphQL resolvers**.
- **Security Layer**: Protects all endpoints using **Spring Security**, implementing **JWT-based authentication**, **Google OAuth2 login**, and **Role-Based Access Control (RBAC)**.
- **Service Layer**: Implements core business logic, including **Argon2-based password hashing**, **Spring Cache** abstraction, and **Transaction Management**.
- **Data Access Layer**: Handles persistence using **Spring Data JPA** with **PostgreSQL**, utilizing repository abstraction and custom JPQL/Native queries. **Flyway** manages schema migrations.
- **Cross-Cutting Concerns**: Uses **Spring AOP** for logging, performance monitoring, and centralized error handling via `@ControllerAdvice`.
- **Domain Model**: Defines core entities (User, Post, Comment, Tag, Review) and enums (**UserRole**) with JPA annotations.

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

- **Secure Authentication**: 
  - **JWT (JSON Web Token)**: Stateless authentication with HMAC SHA-256 signing. [Read more](docs/security/security-details.md).
  - **Google OAuth2**: One-click login for simplified user onboarding.
  - **Argon2 Hashing**: Modern, memory-hard password hashing for maximum security.
  - **RBAC (Role-Based Access Control)**: Granular permissions using **ADMIN** and **USER** roles.
- **Web Security Policies**:
  - **CORS (Cross-Origin Resource Sharing)**: Secure communication for React and JavaFX clients. Prevents unauthorized data reading across origins.
  - **CSRF Protection**: Documented as part of technical requirements (Lab 7) but explicitly **disabled** in the code. This is a secure choice for the project's **stateless JWT model**, as the `Authorization` header provides inherent protection against cross-site request forgery. [Security Deep-dive](docs/security/cors-csrf.md).
- **Persistence & Performance**:
  - **Spring Data JPA**: Abstraction for cleaner data access code. [Read more](docs/persistence/persistence-details.md).
  - **Flyway Migrations**: Version-controlled database schema management.
  - **Spring Cache**: Improving read performance for popular posts and users. [Read more](docs/persistence/caching.md).
  - **Transaction Management**: Ensuring data consistency with `@Transactional`.
  - **Database-Level Pagination**: Efficient data retrieval using `Pageable`.
- **Quality, Monitoring & Infrastructure**:
  - **Asynchronous Processing**: Non-blocking moderation and analytics using `@Async` and `CompletableFuture`. [Read more](docs/monitoring/optimizations.md).
  - **Scheduled Maintenance**: Automated token blacklist cleanup using `@Scheduled`.
  - **Observability Stack**: Real-time monitoring via **Prometheus** and **Grafana**.
  - **Containerization**: Full stack orchestration using **Docker** and **Docker Compose**.
  - **AOP Monitoring**: Automated logging and performance tracking. [Read more](docs/monitoring/aop.md).
  - **Performance Benchmarking**: Detailed analysis of query and cache optimizations. [Read more](docs/persistence/performance-report.md).

---

## Detailed Documentation

For a deeper dive into specific components, refer to the comprehensive documentation modules:

- **[Performance Analysis](docs/monitoring/performance-analysis.md)**: Baseline metrics, bottleneck identification (Epic 1), and VisualVM/JMeter reports.
- **[Security Architecture](docs/security/security-details.md)**: JWT flows, Google OAuth2, Argon2 hashing, and Token Blacklisting.
- **[Web Policies](docs/security/cors-csrf.md)**: CORS preflight handshakes and CSRF immunity strategy.
- **[Persistence Layer](docs/persistence/persistence-details.md)**: JPA repositories, Custom Native SQL, and Flyway.
- **[Performance & Caching](docs/persistence/caching.md)**: Spring Cache strategy and **[Optimization Benchmarks](docs/persistence/performance-report.md)**.
- **[Advanced Optimizations](docs/monitoring/optimizations.md)**: Asynchronous processing, scheduling, and monitoring infrastructure.
- **[API Specification](docs/api/graphql.md)**: GraphQL schema definitions and resolver logic.
- **[Monitoring & AOP](docs/monitoring/aop.md)**: Automated logging and performance aspects.

---

## Project Structure

```text
.
├── docs/                             # Project Documentation
│   ├── security/                    # Authentication & Authorization docs
│   │   ├── security-details.md      # JWT, OAuth2, & RBAC deep-dive
│   │   └── cors-csrf.md             # Web security policy details
│   ├── persistence/                 # Data Layer & Database docs
│   │   ├── persistence-details.md   # Spring Data JPA & Custom Queries
│   │   ├── caching.md               # Spring Cache strategy
│   │   ├── performance-report.md    # Benchmarking & Optimization analysis
│   │   ├── images/                  # Performance report assets
│   │   └── sql/                     # Legacy/Reference SQL scripts
│   │       └── feedDB.sql
│   ├── api/                         # API Specification docs
│   │   └── graphql.md               # GraphQL Integration details
│   └── monitoring/                  # Monitoring & Infrastructure docs
│       ├── aop.md                   # Logging & Monitoring details
│       ├── optimizations.md         # Async, Scheduling, & Docker configuration
│       ├── performance-analysis.md  # Epic 1 Bottleneck identification
│       └── images/                  # Performance & Monitoring screenshots
├── monitoring/                       # Prometheus & Grafana configuration
├── Dockerfile                        # Backend containerization
└── docker-compose.yml                # Full stack orchestration
├── src/main/java/com/amalitech/blogging_platform/
│   ├── BloggingPlatformApplication.java # Spring Boot Entry Point
│   ├── aspect/                      # AOP Aspects (Logging, Performance)
│   ├── config/                      # Configuration (OpenAPI, Cache, etc.)
│   ├── controller/                  # REST Controllers & GraphQL Resolvers
│   ├── dto/                         # Data Transfer Objects
│   ├── exceptions/                  # Global Exception Handlers
│   ├── model/                       # JPA Entity Definitions
│   ├── repository/                  # Spring Data Repositories
│   ├── security/                    # Spring Security, JWT, & OAuth2 Filter
│   └── service/                     # Business Logic & Transactions
├── src/main/resources/              # Assets & Configuration
│   ├── db/
│   │   └── migration/               # Flyway migration scripts
│   │       ├── V001__init_tables.sql
│   │       └── V002__feed_database.sql
│   ├── graphql/                     # GraphQL Schemas (.graphqls)
│   ├── application.yaml             # Main Configuration (uses .env)
│   └── application-dev.yaml         # Dev Profile
└── pom.xml                          # Maven Dependencies
```

---

## Tech Stack & Dependencies

### Core Technologies
- **Framework**: Spring Boot 4.0.2 (**OAuth2 Client**, Spring Data JPA, Spring Cache)
- **Language**: Java 21
- **Database**: PostgreSQL 16+
- **API**: REST (OpenAPI) & GraphQL
- **Security**: **JWT (JJWT)**, **Argon2**

### Key Dependencies
- **Spring Data JPA / Cache / Validation / AOP**: Core Spring components.
- **Spring Security & OAuth2 Client**: Comprehensive security and social login.
- **Flyway**: Database migration management and version control.
- **JJWT**: Implementation for JWT creation and validation.
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
3. Update `.env` with your PostgreSQL, Security, and CORS credentials:
   ```properties
   # Database Configuration
   DB_URL=jdbc:postgresql://localhost:5432/blogging
   DB_USER=your_username
   DB_PASSWORD=your_password

   # Security & JWT
   JWT_SECRET=your_base64_secret_key
   # JWT_EXPIRATION=3600000

   # OAuth2 (Google)
   GOOGLE_CLIENT_ID=your_client_id
   GOOGLE_CLIENT_SECRET=your_client_secret

   # CORS Configuration
   CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
   CORS_ALLOWED_HEADERS=Authorization,Content-Type,X-Requested-With
   CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS
   CORS_ALLOW_CREDENTIALS=true
   ```

### 3. Database Initialization
1. Create a database named `blogging`.
2. Flyway will automatically run migrations on application startup:
   - `V001__init_tables.sql`: Creates all database tables and constraints.
   - `V002__feed_database.sql`: Populates sample data for testing.

> [!NOTE]
> Manual SQL script execution is no longer required. Flyway manages all schema changes through versioned migration files in `src/main/resources/db/migration/`.

### 4. Running the App
```bash
./mvnw spring-boot:run
```

### 5. Running with Docker Compose

For a complete production-like environment (including Monitoring), ensure you have Docker and Docker Compose installed:

1. Build and start the services:
   ```bash
   docker-compose up --build
   ```
2. Access the services:
   - **API**: `http://localhost:8080`
   - **Prometheus**: `http://localhost:9090`
   - **Grafana**: `http://localhost:3000` (Default: admin/admin)

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