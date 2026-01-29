package Enviart.Enviart.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TwoFactorVerificationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            Object needs2FaObj = request.getSession().getAttribute("needs_2fa");
            Object isVerifiedObj = request.getSession().getAttribute("is_2fa_verified");

            boolean needs2Fa = needs2FaObj != null && (Boolean) needs2FaObj;
            boolean isVerified = isVerifiedObj != null && (Boolean) isVerifiedObj;

            if (needs2Fa && !isVerified) {
                String requestURI = request.getRequestURI();

                // Allow access to the verification page itself, logout, and static resources
                boolean isAllowedPath = requestURI.equals("/verify-2fa") ||
                        requestURI.equals("/logout") ||
                        requestURI.equals("/2fa/verify") || // case if I change path
                        requestURI.startsWith("/css/") ||
                        requestURI.startsWith("/js/") ||
                        requestURI.startsWith("/images/") ||
                        requestURI.startsWith("/videos/") ||
                        requestURI.equals("/favicon.ico");

                if (!isAllowedPath) {
                    response.sendRedirect("/verify-2fa");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
