# Performance Bottleneck Analysis

This report establish a performance baseline for the secured blogging backend and identifies critical bottlenecks observed during stress testing.

---

## 1. Initial Performance Baseline

To establish a baseline, system metrics were recorded during application startup and initial interaction (Swagger UI access and authentication).

### Resource Utilization (Idle/Startup)
- **Startup CPU Usage**: Initial peaks reached approximately 20-25% during class loading and context initialization.
- **Startup Memory Footprint**: The application stabilized at a heap size of ~124 MB with approximately 74 MB used after the first minute of uptime.
- **Class Loading**: A total of **25,632** classes were loaded within the first 2.5 minutes, representing the initial Spring Boot and dependency overhead.

#### Visual Evidence: Startup
![Startup Baseline 1](images/StartUp%201%202026-02-13%2015-17-47.png)
*Figure 1: Initial startup class loading and CPU metrics.*

![Startup Baseline 2](images/StartUp%202%202026-02-13%2015-18-24.png)
*Figure 2: Memory stabilization after startup.*

![Post-Startup Activity](images/After%20startup%20(Two%20queries%20Swagget%20UI%20&%20Login)%202026-02-13%2015-19-58.png)
*Figure 3: Metrics after initial Swagger UI and Login requests.*

---

## 2. Identified Performance Bottlenecks

Stress testing revealed that while read operations are highly optimized, the system experiences significant latency during authentication and concurrent write operations.

### Key Observations
1. **Authentication Overhead**: The **Login** operation (identified as "HTTP Request" in JMeter) exhibited the highest max latency (6.2s). This is due to the **Argon2 Password Hashing** algorithm, which is CPU-intensive by design to protect against brute-force attacks. Under high concurrency, these hashing operations compete for CPU cycles, causing significant tail latency.
2. **Memory Pressure**: During simulated load, the heap size rapidly scaled from 126 MB to 881 MB, with used heap peaking near **535 MB**. This indicates massive object allocation during stress tests. 
3. **Write Path Latency**: The **Post Creation** operation also showed tail latency due to the synchronous processing of tags and database persistence.

---

## 3. Stress Test Results Summary

The following data summarizes the system's behavior under peak concurrent load.

### Global Metrics Comparison

| Metric | Baseline (Idle) | Under Load (JMeter) |
| :--- | :--- | :--- |
| **CPU Usage (Max)** | ~5.6% | 55.0% |
| **Used Heap (Avg)** | ~74 MB | ~535 MB |
| **Live Threads** | 17 | 55 |
| **Total Loaded Classes** | **25,632** | 26,473 |

### Response Latency Analysis

| Operation | # Samples | Average (ms) | Max (ms) | Notes |
| :--- | :--- | :--- | :--- | :--- |
| **Login (HTTP Request)** | 106 | 2,534 | 6,245 | **Bottleneck**: CPU-intensive Argon2 hashing |
| **Create Posts** | 113 | 1,064 | 3,175 | **Secondary**: DB Writes & Tag Logic |
| **Get Post Feed** | 111 | 1,147 | 2,941 | **Optimized**: Native Query + Projections |

#### Visual Evidence: Stress Testing
![Performance Testing Start](images/Performance%20Testing%20Start%202026-02-13%2015-20-31.png)
*Figure 4: Initial metrics as the stress test begins.*

![Jmeter Report](images/Jmeter%20report%202026-02-13%2015-27-22.png)
*Figure 5: JMeter aggregate report showing peak latency for Login.*

![After Load Performance](images/After%20Performance%202026-02-13%2015-26-01.png)
*Figure 6: System resource utilization under peak concurrent load.*

![After GC Verification](images/After%20GC%202026-02-13%2015-29-58.png)
*Figure 7: Memory recovery after automatic garbage collection post-test.*

---

## 4. JVM Runtime Configuration

To apply the recommended heap limits during execution, use the following commands depending on your environment.

### Recommended Limits
- **Max Heap (-Xmx768m)**: Set to 768MB because the observed peak used heap during stress tests was **535MB**. Providing a ~40% headroom ensures the application can handle peak surges without constant, performance-degrading Garbage Collection cycles, while remaining within typical lab resource constraints.
- **Min Heap (-Xms256m)**: Ensures the application starts with sufficient memory to avoid early allocation overhead.

### Execution Commands
```bash
# Using Executable JAR
java -Xms256m -Xmx768m -jar target/blogging-platform-0.0.1-SNAPSHOT.jar

# Using Maven Spring Boot Plugin
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xms256m -Xmx768m"
```

---

## 5. Optimized Features & Future Improvements

### Existing Optimizations
The current system already leverages several performance best practices:
- **Tuned Pagination**: Default page size (10) is already balanced for optimized memory usage and UX.
- **Security Protocols**: Argon2 hashing is currently using robust Spring Security defaults, ensuring a high level of security without unnecessary computational waste.
- **Read Optimization**: Feed retrieval is already optimized using **Native Queries** and **Projections** to avoid N+1 issues.

### Recommended Simple Improvements
- **Database Indexing**: Apply B-Tree indexes to `tags.name` and `posts.title` to further speed up join and search filtering.
- **Connection Timeout Tuning**: Adjust the HikariCP `connection-timeout` in `application.yaml` to better manage threads waiting for the pool of 10 connections during concurrency spikes.
- **Cache Strategy**: Add Time-To-Live (TTL) headers or short-term caching to the Feed response to reduce the impact of repeated high-concurrency read requests.
