package com.betolara1.user.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.betolara1.user.DTO.response.StandardErrorDTO;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalHandlerException {

    // TRATAMENTO DE RECURSO NÃO ENCONTRADO (404)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardErrorDTO> handleRecursoNaoEncontrado(NotFoundException ex, HttpServletRequest request) {
        
        StandardErrorDTO erro = new StandardErrorDTO(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Não Encontrado",
            ex.getMessage(),
            request.getRequestURI() // Mostra qual URL o usuário tentou acessar
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    // TRATAMENTO DE ACESSO NÃO AUTORIZADO (401)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<StandardErrorDTO> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        
        StandardErrorDTO erro = new StandardErrorDTO(
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value(),
            "Não Autorizado",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    // TRATAMENTO DE ACESSO PROIBIDO (403)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<StandardErrorDTO> handleForbidden(ForbiddenException ex, HttpServletRequest request) {
        
        StandardErrorDTO erro = new StandardErrorDTO(
            LocalDateTime.now(),
            HttpStatus.FORBIDDEN.value(),
            "Proibido",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    // TRATAMENTO GENÉRICO (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardErrorDTO> handleGenericException(Exception ex, HttpServletRequest request) {
        
        StandardErrorDTO erro = new StandardErrorDTO(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro Interno do Servidor",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
