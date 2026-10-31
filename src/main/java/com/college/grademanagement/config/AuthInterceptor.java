package com.college.grademanagement.config;

import com.college.grademanagement.dto.UserDto;
import com.college.grademanagement.model.Role;
import com.college.grademanagement.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Allow static resources and auth endpoints
        if (!path.startsWith("/api/") ||
            path.equals("/api/auth/login") ||
            path.equals("/api/auth/logout") ||
            path.startsWith("/h2-console")) {
            return true;
        }

        // Allow pre-flight OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        UserDto currentUser = (session != null) ? (UserDto) session.getAttribute(AuthService.SESSION_USER_KEY) : null;

        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":401,\"message\":\"Authentication required. Please log in.\"}");
            return false;
        }

        // Role-Based Authorization
        // Admin has full access.
        // Teacher has read-only access to students, subjects, exams, and can manage marks & view results.
        if (currentUser.getRole() == Role.TEACHER) {
            boolean isModifying = "POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method);

            // Modifying students, teachers, subjects, or examinations requires ADMIN role
            if (isModifying && (path.startsWith("/api/students") ||
                               path.startsWith("/api/teachers") ||
                               path.startsWith("/api/subjects") ||
                               path.startsWith("/api/examinations"))) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":403,\"message\":\"Access denied. Admin role required for this action.\"}");
                return false;
            }
        }

        return true;
    }
}
