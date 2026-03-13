package com.betolara1.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private final String SECRET_KEY = "mySuperSecretKeyForTestingWhichNeedsToBeVeryLongIndeedToMatchTheAlgorithmRequirement";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Injetando a chave secreta mockada no filtro
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "jwtSecret", SECRET_KEY);
    }

    @Test
    void shouldPermitPublicRoutesWithoutToken() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/auth/login");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void shouldRejectWhenNoTokenProvided() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/orders/create");
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void shouldRejectWhenInvalidTokenProvided() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/orders/create");
        when(request.getHeader("Authorization")).thenReturn("Bearer tokenInvalidoOuExpirado");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void shouldAuthenticateAndInjectHeadersWhenTokenIsValid() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/orders/create");

        // Geração de um token JWT válido para o teste
        String validToken = Jwts.builder()
                .setSubject("testuser")
                .claim("userId", 100L)
                .claim("role", "ADMIN")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hora de validade
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
        verify(filterChain, times(1)).doFilter(requestCaptor.capture(), eq(response));

        // Verificando se os headers customizados foram injetados no HttpServletRequestWrapper
        HttpServletRequest modifiedRequest = requestCaptor.getValue();
        assertEquals("100", modifiedRequest.getHeader("X-User-Id"));
        assertEquals("ADMIN", modifiedRequest.getHeader("X-User-Role"));
        
        // Verifica se ainda pega headers originais
        when(request.getHeader("Host")).thenReturn("localhost");
        assertEquals("localhost", modifiedRequest.getHeader("Host"));
    }
}
