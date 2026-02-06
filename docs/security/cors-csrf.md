# CORS & CSRF Policies

This document details the cross-origin communication rules and CSRF protection strategies implemented in the Blogging Platform.

---

## 1. Cross-Origin Resource Sharing (CORS)

CORS is a security mechanism that allows or restricts web applications running at one origin from interacting with resources from a different origin.

### Implementation in the Backend
The platform uses a global `CORSConfig` and `CORSProperties` to manage cross-origin access via the `CorsFilter`.

#### 1. Preflight Requests (OPTIONS)
Browsers automatically send a "preflight" request using the `OPTIONS` method before executing "non-simple" requests (e.g., requests with `Content-Type: application/json` or custom headers like `Authorization`).
- **Success Criteria**: The server must respond with the correct `Access-Control-Allow-*` headers.
- **Backend Role**: Our configuration ensures that the `CorsFilter` intercepts these OPTIONS requests early in the security filter chain and responds with a `200 OK` and the appropriate allowed origins/methods.

#### 2. Key CORS Headers Configured:
- **`Access-Control-Allow-Origin`**: Specifies which external domains can access the API. For development, this is often set to the React dev server (e.g., `http://localhost:3000`).
- **`Access-Control-Allow-Methods`**: Controls which HTTP verbs (GET, POST, PUT, DELETE) are permitted.
- **`Access-Control-Allow-Headers`**: Critical for JWT, as it must allow the `Authorization` header.
- **`Access-Control-Allow-Credentials`**: Set to `true` to allow the passing of the secure cookies required for the OAuth2 handshake.

---

## 2. Cross-Site Request Forgery (CSRF)

CSRF is an attack where a malicious site tricks a user's browser into sending a request to your application using the user's active session.

### Stateless API "Immunity"
The Blogging Platform disables CSRF because it uses a **stateless** authentication model (JWT).

#### Why Statelessness Blocks CSRF:
- **Traditional CSRF**: Relies on the browser automatically attaching **Cookies** to every request made to a specific domain. If a user is logged into `bank.com`, a malicious `evil.com` can trigger a POST request to `bank.com/transfer`, and the browser will "helpfully" include the authentication cookie.
- **JWT Protection**: Since our JWT is stored in **Local Storage** or **Memory** (not in a standard cookie), the browser **does not** automatically attach it to requests. The frontend must manually retrieve the token and add the `Authorization: Bearer <token>` header. A malicious site cannot access your local storage, so it cannot send a valid authenticated request.

### Secure JWT Storage & XSS
While JWT is immune to CSRF, it is susceptible to **XSS (Cross-Site Scripting)** if stored in Local Storage.
- **Mitigation**: We ensure all inputs are sanitized and output is encoded. For higher security environments, storing JWT in **HttpOnly** cookies is recommended, which would then require re-enabling CSRF protection.

### When to Enable CSRF
If the application were to use **Session Cookies** for authentication in a browser-based frontend:
- **Required**: A CSRF token would be generated on the server and must be included in every stateful (POST, PUT, DELETE) request.
- **Spring Security Benefit**: Spring provides `CookieCsrfTokenRepository` to handle this automatically for SPAs.

---

## 3. Comparison & Best Practices

| Policy | Primary Goal | Implementation Layer |
|--------|--------------|----------------------|
| **CORS** | Allow legitimate cross-origin requests | Browser & Server handshake |
| **CSRF** | Prevent unauthorized action execution | Server-side token validation |

**Security Best Practices Followed:**
- Use **HTTPS** for all communications to protect tokens in transit.
- Set **SameSite=Strict** or **Lax** for any cookies used in OAuth2 flows.
- Keep JWTs short-lived and implement refresh token rotation if needed.
