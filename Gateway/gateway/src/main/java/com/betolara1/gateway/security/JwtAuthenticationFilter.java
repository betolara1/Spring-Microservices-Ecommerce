package com.betolara1.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${secret.key}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Pula validação para rotas de login
        if (path.startsWith("/auth") || 
            path.startsWith("/users/register") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/webjars") ||
            path.startsWith("/actuator") ||
            path.equals("/error")) {
            try {
                filterChain.doFilter(request, response);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        String token = authHeader.substring(7);

        try {
            // Usa a biblioteca JJWT para validar e abrir o token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Pega o ID ou username do usuário (depende de como vc montou o token)
            String userId = claims.getSubject();
            String roles = claims.get("roles", String.class);

            // IMPORTANTE: O Gateway cria uma NOVA requisição colocando headers simples e encaminha.
            // Para Servlet Filter, normalmente usamos um HttpServletRequestWrapper para injetar headers.
            HttpServletRequestWrapper modifiedRequest = new HttpServletRequestWrapper(request) {
                @Override
                public String getHeader(String name) {
                    if ("X-User-Id".equalsIgnoreCase(name)) return userId;
                    if ("X-User-Roles".equalsIgnoreCase(name)) return roles;
                    return super.getHeader(name);
                }
            };

            // Passa a requisição limpa pra frente (pro Gateway fazer o roteamento lb://)
            filterChain.doFilter(modifiedRequest, response);

        } catch (Exception e) {
            // Token expirado ou inválido
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
