package com.capsule.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private StringRedisTemplate redis;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        String ip = getClientIp(request);

        // Rule 1: POST /api/capsule/create
        if ("POST".equals(method) && path.equals("/api/capsule/create")) {
            if (isRateLimited("rl:create:" + ip, 5, 3600)) {
                writeError(response, 429, "Too many capsules created. Try again later.", "RATE_LIMITED");
                return;
            }
        }

        // Rule 2: POST /api/capsule/{token}/files
        else if ("POST".equals(method) && path.matches("/api/capsule/[^/]+/files")) {
            String token = extractTokenFromPath(path, 3);
            if (isRateLimited("rl:upload:" + token, 20, -1)) {
                writeError(response, 429, "Upload limit reached for this capsule.", "RATE_LIMITED");
                return;
            }
            // Size quota check — done in service layer, not here
        }

        // Rule 3: POST /api/capsule/{token}/verify
        else if ("POST".equals(method) && path.matches("/api/capsule/[^/]+/verify")) {
            String token = extractTokenFromPath(path, 3);
            if (isRateLimited("rl:verify:" + token + ":" + ip, 10, 3600)) {
                writeError(response, 429, "Too many attempts. Please wait.", "RATE_LIMITED");
                return;
            }
        }

        // Rule 4: GET /api/capsule/recover
        else if ("GET".equals(method) && path.equals("/api/capsule/recover")) {
            String email = request.getParameter("email");
            if (email != null && isRateLimited("rl:recover:" + email, 3, 3600)) {
                writeError(response, 429, "Too many recovery attempts.", "RATE_LIMITED");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Redis INCR + EXPIRE pattern.
     * ttlSeconds = -1 means no TTL (count is permanent, e.g. total upload count per capsule)
     * Returns true if limit exceeded.
     */
    private boolean isRateLimited(String key, int maxRequests, long ttlSeconds) {
        Long count = redis.opsForValue().increment(key);
        if (count == null) return false;
        
        // Only set expire on the first request if ttlSeconds is positive
        if (count == 1 && ttlSeconds > 0) {
            redis.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }
        
        return count > maxRequests;
    }

    private void writeError(HttpServletResponse response, int status,
                             String message, String code) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(
            "{\"message\":\"" + message + "\",\"code\":\"" + code + "\"}"
        );
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String extractTokenFromPath(String path, int segmentIndex) {
        String[] parts = path.split("/");
        return parts.length > segmentIndex ? parts[segmentIndex] : "unknown";
    }
}
