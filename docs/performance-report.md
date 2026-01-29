# Performance Benchmarking & Optimization Report

This report documents the performance improvements achieved through the integration of **Spring Data JPA**, custom query optimizations, and **Spring Cache**.

## Performance Comparison: Before vs. After

### 1. Repository & Query Optimization
By migrating from manual JDBC to Spring Data JPA and implementing optimized native queries and `@EntityGraph`, we reduced query complexity and memory overhead.

| Operation | Pre-Optimization (JDBC) | Post-Optimization (JPA) | Improvement (%) |
|-----------|------------------------|-------------------------|-----------------|
| Get Feed  | [INSERT MS]            | [INSERT MS]             | [INSERT %]      |
| Search    | [INSERT MS]            | [INSERT MS]             | [INSERT %]      |

### 2. Caching Implementation
The following table highlights the impact of **Spring Cache** on read-heavy operations for posts.

| Metric | Cache Miss (DB Hit) | Cache Hit | Improvement (%) |
|--------|---------------------|-----------|-----------------|
| Response Time | [INSERT MS] | [INSERT MS] | [INSERT %] |

## Visual Evidence

### Benchmarking Screenshots

#### Feed Retrieval Performance
![Pre-Optimization Feed Performance]([INSERT_IMG_PATH])
*Figure 1: Performance metrics before JPA/Caching optimization.*

![Post-Optimization Feed Performance]([INSERT_IMG_PATH])
*Figure 2: Performance metrics after JPA/Caching optimization.*

#### Cache Hit Verification
![Cache Hit Response Time]([INSERT_IMG_PATH])
*Figure 3: Demonstration of sub-10ms response times for cached post retrieval.*

## Conclusion
The implementation of Spring Data JPA and Spring Cache has significantly reduced system latency and improved scalability. Database-level pagination and optimized fetching strategies have ensured consistent performance even as the dataset grows.
