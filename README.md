# Blogging Platform

A web-based blogging platform built with **Spring Boot** and **PostgreSQL**. The application transforms a standard backend into a layered system suitable for enterprise-grade development, featuring **RESTful** and **GraphQL APIs**, comprehensive **validation**, **centralized exception handling**, and **AOP-based monitoring**.

---

## System Architecture

The platform follows a layered architecture to ensure separation of concerns and maintainability:

- **Presentation Layer**: Exposes data via **REST controllers** and **GraphQL resolvers**.
- **Service Layer**: Implements core business logic, including **Argon2-based password hashing** and transaction coordination.
- **Data Access Layer (DAO)**: Handles persistence using manual **JDBC** with **PostgreSQL**, utilizing optimized SQL queries and custom mappers.
- **Cross-Cutting Concerns**: Uses **Spring AOP** for logging, performance monitoring, and centralized error handling via `@ControllerAdvice`.
- **Domain Model**: Defines core entities (User, Post, Comment, Tag, Review).

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
- **Service (Model Logic)**: Contains the core business rules and transaction boundaries. It orchestrates calls to the Data Access Layer.
- **DAO (Model Data)**: The Data Access Object layer handles direct interactions with the PostgreSQL database using JDBC, abstracting the SQL complexity from the service layer.

### 3. Aspect-Oriented Programming (AOP)
Cross-cutting concerns are modularized using Spring AOP, preventing code duplication in business methods.
- **Logging**: The `LoggingAspect` automatically logs method entry, exit, arguments, and exceptions for all service methods.
- **Monitoring**: The `PerformanceAspect` tracks basic execution metrics, logging warnings for slow methods (>1s) to aid in performance tuning.

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
- **Advanced Data Operations**:
  - **Manual Pagination**: Database-level pagination implemented in DAOs.
  - **Soft Deletion**: All entities support soft deletion (marking records as deleted without removing them).
- **Quality & Monitoring**:
  - **Validation**: Strict input validation using Bean Validation.
  - **AOP Monitoring**: Automated logging and performance tracking. [Read more](docs/aop.md).
  - **Performance Metrics**: Real-time tracking of execution time for critical methods.
  - **OpenAPI Documentation**: Interactive API testing with Swagger UI.

---

## Project Structure

```text
.
├── docs/                             # Documentation & SQL scripts
│   ├── aop.md                       # AOP Implementation details
│   ├── database-design.md           # Conceptual, Logical, & Physical models
│   ├── graphql.md                   # GraphQL Integration details
│   ├── performance-report.md        # Benchmarking & Optimization analysis
│   ├── script.sql                   # Schema creation script
│   └── feedDB.sql                   # Sample data script
├── src/main/java/com/amalitech/blogging_platform/
│   ├── BloggingPlatformApplication.java # Spring Boot Entry Point
│   ├── aspect/                      # AOP Aspects (Logging, Performance)
│   ├── config/                      # Configuration (OpenAPI, etc.)
│   ├── controller/                  # REST Controllers & GraphQL Resolvers
│   ├── dao/                         # JDBC Data Access Objects
│   ├── dto/                         # Data Transfer Objects
│   ├── exceptions/                  # Global Exception Handlers
│   ├── model/                       # Entity Definitions
│   └── service/                     # Business Logic Layer
├── src/main/resources/              # Assets & Configuration
│   ├── graphql/                     # GraphQL Schemas (.graphqls)
│   ├── application.yaml             # Main Configuration (uses .env)
│   └── application-dev.yaml         # Dev Profile
└── pom.xml                          # Maven Dependencies
```

---

## Tech Stack & Dependencies

### Core Technologies
- **Framework**: Spring Boot 4.0.1 (preview)
- **Language**: Java 21
- **Database**: PostgreSQL 16+
- **API**: REST & GraphQL
- **Security**: Argon2 JVM

### Key Dependencies
- **Spring Web / GraphQL / Validation / AOP**: Core Spring components.
- **Argon2 JVM**: For secure password hashing.
- **Dotenv-Java**: For environment variable management.
- **Lombok**: Boilerplate reduction.
- **Springdoc OpenApi**: Automated Swagger UI generation.

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