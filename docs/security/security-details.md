# Security Implementation Details

The platform implements a robust security layer powered by **Spring Security**, providing stateless authentication via **JWT** and social login via **Google OAuth2**.

---

## 1. Authentication Flows

### JWT-Based Authentication (Stateless)
Standard login requests go through the `/api/v1/auth/login` endpoint.
1. **Credentials Verification**: The application verifies the username and password against the database (using **Argon2** hashing).
2. **Token Generation**: Upon success, a **JWT (JSON Web Token)** is generated using the **JJWT** library's `Jwts.builder()` pattern.
   - **Signed Architecture**: Uses `Keys.hmacShaKeyFor()` to create a secure signing key from the `JWT_SECRET` environment variable.
   - **Claims Structure**:
     - `sub` (Subject): Extracted from `UserDetails.getUsername()`.
     - `roles`: Assigned authorities (e.g., `ROLE_USER`, `ROLE_ADMIN`).
     - `iat` (Issued At): Core timestamp for auditing.
     - `exp` (Expiration): Default TTL of 1 hour (3,600,000ms).
3. **Subsequent Requests**: Clients must include the token in the `Authorization: Bearer <token>` header.
4. **JWT Filter**: The `JWTAuthenticationFilter` intercepts requests, validates the signature via the **HMAC lookup**, check for expiration, and populates the `SecurityContext`.

### Google OAuth2 Login
Integrated via `spring-boot-starter-security-oauth2-client`:
1. **Authorization**: Users are redirected to Google's consent screen.
2. **Callback Handling**: Google returns an authorization code, which the backend exchanges for user details.
3. **User Persistence**: The `OAuth2AuthenticationSuccessHandler` checks the `email` from Google against the database. If new, it creates a `User` entity with the `UserRole.USER` enum default.
4. **JWT Generation & Redirect**: After successful persistence, the handler generates a JWT and performs a **Safe Redirect** to the frontend (e.g., `http://localhost:3000/auth/success?token=...`), allowing the client to consume the stateless token immediately.

---

## 2. Hybrid Authentication: JWT (Stateless) + OAuth2 (Stateful)

A common challenge in modern backends is combining the stateless nature of **JWT** with the requirements of **OAuth2**, which typically relies on server-side sessions to track authorization requests.

### The Solution: Cookie-Based Request Repository
Instead of using standard Spring Security sessions (which would break statelessness), the platform implements a hybrid approach:

- **HttpCookieOAuth2AuthorizationRequestRepository**: This custom repository stores the initial OAuth2 authorization request (and state) in short-lived, encrypted **Cookies** rather than an `HttpSession`.
- **Flow**:
    - The client initiates OAuth2.
    - The backend sets a cookie with the request details and redirects to Google.
    - Upon returning, the backend reads the cookie to validate the state and complete the handshake.
    - Once authenticated, the backend generates a **JWT** and discards the temporary cookies.
- **Benefit**: The application remains **stateless** throughout the entire lifecycle, making it horizontally scalable while still supporting complex OAuth2 handshakes.

---

## 3. Web Security Policies

For details on how we protect against cross-origin attacks and maintain stateless security, see:
- **[CORS & CSRF Policies](cors-csrf.md)**: Detailed handling of preflight requests and stateless session immunity.

---

## 3. Role-Based Access Control (RBAC)

The system uses the `UserRole` **Enum** to enforce access policies. Unlike a separate database entity, this enum-based approach ensures type safety and performance.

| Role | Access Level | Restricted Endpoints Examples |
|------|--------------|------------------------------|
| **ADMIN** | Full system control | Delete any post, Manage Users, View Logs |
| **USER** | Standard user access | Create Posts, Edit own Posts, Comment |

### Access Control Rules
The `SecurityFilterChain` defines granular access rules:
- **Public**: `/api/v1/auth/**`, `/swagger-ui/**`, `/graphiql`, `/api/v1/post/feed`.
- **Authenticated**: All other `/api/**` endpoints.
- **Method-Level Security**: `@PreAuthorize("hasRole('ADMIN')")` is used to protect sensitive mutations in the business layer.

---

## 3. Password Hashing with Argon2

To protect against brute-force and rainbow table attacks, the system uses **Argon2**, the winner of the Password Hashing Competition (PHC).
- **Configuration**: Managed via `PasswordConfig` using Spring Security's memory-hard recommendations.
- **Why Argon2?**: Better security compared to BCrypt or SCrypt due to configurable memory cost and parallelism, effectively mitigating GPU/ASIC-based attacks.

---

---

## 5. DSA and Security Architecture

The platform categorizes security mechanisms under **Data Structures and Algorithms (DSA)** to ensure high performance:

| Category | Component | Implementation Detail |
|----------|-----------|-----------------------|
| **Algorithm** | **Argon2** | Memory-hard password hashing to resist GPU/ASIC cracking. |
| **Data Structure** | **ConcurrentHashMap** | Provides **O(1)** lookup for blacklisted tokens, ensuring thread-safe concurrent access without locking the entire map. |
| **Logic Flow** | **Filter Chain** | A sequential processing algorithm that decides request fate before it reaches the `@Controller`. |
| **Cache Algorithm** | **Eviction Strategy** | Scheduled cleanup using `entrySet().removeIf()` every 10 minutes to maintain memory efficiency. |

---

## 6. Secure Logout & Token Revocation

Since JWT is stateless, "logging out" on the client side only removes the token from storage. To prevent the use of stolen tokens before they expire, the backend implements a manual revocation layer.

1. **Logout Request**: The client sends a request to `/api/v1/auth/logout`.
2. **Blacklisting**: The backend extracts the token and its remaining TTL (Time To Live).
3. **Storage**: The token is added to the **ConcurrentHashMap**-based blacklist.
4. **Enforcement**: Any subsequent request with this token will be rejected by the `JWTAuthenticationFilter` with a `401 Unauthorized` status.

---

## 5. Security Performance

Real-world monitoring shows that security overhead (JWT validation and blacklisting lookup) is minimal:
- **JWT Decoding & Validation**: ~1-2ms
- **Blacklist Lookup (O(1))**: <1ms
- **Total Security Impact**: Negligible compared to network latency and DB operations.
