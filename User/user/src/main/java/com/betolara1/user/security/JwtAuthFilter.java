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

    // O CONTRUTOR UserDetailsService É DO SRPING BOOT
    // ELE VAI PROCURAR A CLASSE QUE IMPLEMENTA UserDetailsService E INJETAR ELA
    // AQUI, ASSIM PODEMOS USAR O MÉTODO
    // loadUserByUsername PARA CARREGAR O USUÁRIO PELO NOME DE USUÁRIO EXTRAÍDO DO
    // TOKEN
    private final UserDetailsService userDetailsService;
    public JwtAuthFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private final JwtUtil JwtUtil = new JwtUtil();

    // METODO CRIA OBRIGATORIO POR ESTENDER DE OncePerRequestFilter
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // IGNORAR AS REQUISIÇÕES DE DOCUMENTAÇÃO E ATUATOR PARA NÃO PRECISAR DE
        // AUTENTICAÇÃO NELAS
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

        // EXTRAIR O TOKEN DO HEADER Authorization
        // CONDIÇÃO PARA SABER SE O USUARIO FOI AUTORIZADO
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        // SE CHEGOU AQUI, SIGNIFICA QUE O USUÁRIO TEM UM TOKEN, ENTÃO VAMOS EXTRAIR O
        // USUÁRIO DO TOKEN E VALIDAR O TOKEN
        String token = authHeader.substring(7);
        String username = JwtUtil.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (JwtUtil.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);

    }
}