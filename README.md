# Blogging Platform - Database Layer

A high-performance relational database-driven blogging platform built with JavaFX and PostgreSQL. This project focuses on efficient data modeling, CRUD operations, and performance optimization using indexing and in-memory caching.

---

## System Architecture

The platform follows a layered architecture to ensure separation of concerns:
- **View Layer**: Provide user interface for interaction
- **Controller Layer**: Manages JavaFX UI interactions.
- **Service Layer**: Implements business logic and orchestration (includes in-memory caching).
- **DAO (Data Access Object)**: Handles database persistence and optimized SQL execution.
- **Model**: Defines core entities (User, Post, Comment, Tag, Review).



## Features

- **User Management**: Secure registration  with Argon2 password hashing.
- **Content Creation**: Full CRUD operations for blog posts.
- **Interactive Feedback**: Add, view, and manage comments and nested replies.
- **Tagging System**: Organize posts with dynamic tag assignment and filtering.
- **Review System**: Star-based ratings (1-5) for curated post feedback.
- **Advanced Search**: Case-insensitive keyword search optimized with GIN indexing.

---


## Project Structure

```text
.
├── docs/                             # Documentation & SQL scripts
│   ├── database-design.md           # Conceptual, Logical, & Physical models
│   ├── performance-report.md        # Benchmarking & Optimization analysis
│   ├── nosql-justification.md       # Theoretical NoSQL application
│   ├── script.sql                   # Schema creation script
│   ├── feedDB.sql                   # Sample data script
│   ├── ERD.png                      # Database diagram
│   └── stats-performance.png        # Benchmarking results screenshot
├── src/main/java/amalitech/blog/    # Source Code
│   ├── Main.java                    # JavaFX Application Entry Point
│   ├── PerformanceMain.java          # Performance Benchmarking Entry Point
│   ├── ApplicationContext.java      # Application state management
│   ├── controller/                  # JavaFX UI Controllers
│   │   ├── auth/                    # Login & SignUp logic
│   │   └── posts/                   # Post creation & details logic
│   ├── dao/                         # Data Access Objects
│   │   ├── DatabaseConnection.java  # JDBC connection pooling
│   │   └── enums/                   # Column name enums
│   ├── service/                     # Service Layer (Business Logic)
│   ├── model/                       # Entity Definitions
│   ├── dto/                         # Data Transfer Objects
│   └── utils/                       # Validation & Performance tools
├── src/main/resources/              # Assets & Views
│   ├── amalitech/blog/view/         # FXML UI Definitions
│   └── amalitech/blog/css/          # Stylesheets
└── pom.xml                          # Maven Dependencies
```

---

## Tech Stack & Dependencies

### Core Technologies
- **Java**: Version 21
- **Spring boot**: Version 4.0.1

- **Database**: PostgreSQL 16+
- **Security**: Argon2 JVM (Password Hashing)
- **Configuration**: Dotenv Java

### Dependencies (Maven)
- **Spring boot web mvc**
- **Spring boot graphql**
- **Spring boot validation**
- **Spring doc openApi/Swagger-ui**: Version 3.0.1
- `postgresql`: JDBC driver for database connectivity.
- `lombok`: Boilerplate reduction for models and DTOs.
- `dotenv-java`: Secure environment variable management.
- `argon2-jvm`: Advanced password hashing implementation.
- `slf4j-api` & `logback-classic`: Comprehensive logging framework.
- `jna`: Java Native Access for low-level library support.

---

## Setup & Installations

### 1. Prerequisites
- JDK 21
- PostgreSQL Installed and Running
- Maven 3.8+

### 2. Repository Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/angebhd/amalitech-lab-spring-boot-blogging-platform.git
   cd amalitech-lab-spring-boot-blogging-platform
   ```
2. Create your environment file:
   ```bash
   cp .env.example .env
   ```
3. Open `.env` and fill in your PostgreSQL credentials:
   ```properties
   DB_URL=jdbc:postgresql://localhost:5432/blogging
   DB_USER=your_username
   DB_PASSWORD=your_password
   ```

### 3. Database Initialization
1. Create a database named `blogging`.
2. Execute the main schema script to create tables and indexes:
   ```bash
   psql -d blogging -f docs/script.sql
   ```
3. Execute the sample data script to populate the database:
   ```bash
   psql -d blogging -f docs/feedDB.sql
   ```

Graph error handling
DTOs: validation & Documentation
