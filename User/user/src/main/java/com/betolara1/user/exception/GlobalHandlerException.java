package com.betolara1.user.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.betolara1.user.DTO.response.StandardErrorDTO;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
                request.getRequestURI());

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
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    // TRATAMENTO DE ERROS DE VALIDAÇÃO (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardErrorDTO> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        ValidationErrorDTO errorMessages = new ValidationErrorDTO();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorMessages.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        StandardErrorDTO erro = new StandardErrorDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação",
                errorMessages.toString(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // TRATAMENTO DE VIOLAÇÃO DE INTEGRIDADE (409) - Ex: username duplicado
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardErrorDTO> handleDataIntegrityException(DataIntegrityViolationException ex, HttpServletRequest request) {

        StandardErrorDTO erro = new StandardErrorDTO(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflito de Dados",
                "Violação de integridade nos dados (ex: chave duplicada ou erro de restrição).",
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    // TRATAMENTO DE RECURSO NÃO ENCONTRADO NO ENDPOINT (404)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<StandardErrorDTO> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {

        StandardErrorDTO erro = new StandardErrorDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Endpoint Não Encontrado",
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    // TRATAMENTO GENÉRICO (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardErrorDTO> handleGenericException(Exception ex, HttpServletRequest request) {

        StandardErrorDTO erro = new StandardErrorDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno do Servidor",
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
