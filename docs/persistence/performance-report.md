# Performance Benchmarking & Optimization Report

This report documents the performance improvements achieved through the integration of **Spring Data JPA**, custom query optimizations, and **Spring Cache**.

## Performance Comparison: Before vs. After

### 1. Repository & Query Optimization
By staying within Spring Data JPA and correctly implementing `@EntityGraph`, we resolved critical N+1 select issues, drastically reducing database round-trips and response times.

| Operation | Unoptimized JPA (with N+1 issues) | Optimized JPA (with EntityGraph) | Improvement (%) |
|-----------|-------------------------------------|---------------------------------------|-----------------|
| Standard Post Retrieval | 1.038s | 420ms | ~59.5% |
| Comment Retrieval | 545ms | 335ms | ~38.5% |

### 2. Caching Implementation
The following table highlights the impact of **Spring Cache** on read-heavy operations for posts.

| Metric | Cache Miss (DB Hit) | Cache Hit | Improvement (%) |
|--------|---------------------|-----------|-----------------|
| Get Single Post | 97ms | 16ms | ~83.5% |
| Get Feed Post (Cached) | 1.07s | 74ms | ~93.1% |

## Visual Evidence

### Benchmarking Screenshots

#### Standard Post Retrieval & N+1 Optimization
![Pre-Optimization Post Fetching](images/N+1%20while%20fetching%20posts.png)
*Figure 1: Performance metrics for standard post fetching experiencing N+1 select issues (1.038s).*

![Post-Optimization Post Fetching](images/N+1%20while%20fetching%20posts%20(solved).png)
*Figure 2: Performance improvement for standard posts after implementing EntityGraph (420ms).*

#### Comment Retrieval Optimization
![Pre-Optimization Comment Fetching](images/comments%20n+1%20problem.png)
*Figure 3: N+1 problem identified when fetching comments (545ms).*

![Post-Optimization Comment Fetching](images/comment%20N+1%20solved.png)
*Figure 4: Performance gain after solving N+1 in comment retrieval (335ms).*

#### Cache Hit Verification (Single Post)
![Single Post Before Caching](images/single%20post%20fetching%20before%20caching.png)
*Figure 5: Initial request for a single post (97ms).*

![Single Post After Caching](images/single%20post%20fetching%20after%20caching.png)
*Figure 6: Subsequent request for a single post served from cache (16ms).*

#### Feed Caching Optimization
![Feed Cache Miss](images/Fetching%20post%20second%20time%20(data%20from%20cache).png)
*Figure 7: Initial feed post retrieval (Cache Miss - 1.07s).*

![Feed Cache Hit](images/Screenshot%20from%202026-01-29%2021-30-11.png)
*Figure 8: High-speed feed retrieval served from cache (Cache Hit - 74ms).*

## Conclusion
The implementation of Spring Data JPA and Spring Cache has significantly reduced system latency and improved scalability. Database-level pagination and optimized fetching strategies have ensured consistent performance even as the dataset grows.
