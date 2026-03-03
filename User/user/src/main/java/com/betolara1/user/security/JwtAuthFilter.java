package com.betolara1.user.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final org.springframework.web.servlet.HandlerExceptionResolver exceptionResolver;

    public JwtAuthFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil,
            @org.springframework.beans.factory.annotation.Qualifier("handlerExceptionResolver") org.springframework.web.servlet.HandlerExceptionResolver exceptionResolver) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String path = request.getRequestURI();
            String method = request.getMethod();
            if ("OPTIONS".equalsIgnoreCase(method)
                    || path.startsWith(request.getContextPath() + "/v3/api-docs")
                    || path.startsWith(request.getContextPath() + "/swagger")
                    || path.startsWith(request.getContextPath() + "/webjars")
                    || path.startsWith(request.getContextPath() + "/swagger-ui")
                    || path.equals(request.getContextPath() + "/swagger-ui.html")
                    || path.startsWith(request.getContextPath() + "/actuator")) {
                filterChain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            exceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
