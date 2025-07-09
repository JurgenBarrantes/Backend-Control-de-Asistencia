package com.systems.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationHelper {

    private final JwtTokenUtil jwtTokenUtil;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .findFirst()
                    .map(authority -> authority.getAuthority())
                    .orElse(null);
        }
        return null;
    }

    public boolean isAdmin() {
        String role = getCurrentUserRole();
        return "ADMIN".equals(role);
    }

    public boolean isTeacher() {
        String role = getCurrentUserRole();
        return "TEACHER".equals(role);
    }

    public boolean isStudent() {
        String role = getCurrentUserRole();
        return "STUDENT".equals(role);
    }
}
