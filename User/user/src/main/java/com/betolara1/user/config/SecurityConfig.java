package com.betolara1.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.betolara1.jwt_package.security.JwtAuthFilter;

// Nome customizado para evitar conflito com o SecurityConfig do jwt-package
// O jwt-package já fornece PasswordEncoder e AuthenticationManager como beans
@Configuration("userSecurityConfig")
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // Não precisamos mais injetar o UserDetailsService aqui no construtor
    // O Spring vai achá-lo sozinho se ele tiver a anotação @Service na classe dele
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // Configuração de segurança do Spring Security
    // Aqui definimos quais endpoints são públicos e quais precisam de autenticação,
    // além de configurar o filtro de autenticação JWT
    // O método securityFilterChain é obrigatório para configurar o Spring Security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                        "/auth/**",
                        "/v3/api-docs/**",
                        "/v3/api-docs",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/actuator/**").permitAll().anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
