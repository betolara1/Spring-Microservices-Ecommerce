package com.betolara1.Payments.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// O StandardErrorDTO é um DTO que representa a estrutura de um erro padrão para as respostas de erro da API.
// Ele contém informações como timestamp, status HTTP, mensagem de erro e o caminho da requisição que causou o erro.
// Esse DTO é útil para padronizar as respostas de erro e facilitar o tratamento de erros no cliente, 
// além de fornecer informações úteis para depuração e monitoramento.
// O GlobalHandlerException é uma classe que vai usar o StandardErrorDTO para criar respostas de erro personalizadas

@Data                // Cria Getters, Setters, toString, equals e hashCode automaticamente
@AllArgsConstructor  // Cria o construtor com todos os argumentos
@NoArgsConstructor   // Cria o construtor vazio (necessário para algumas bibliotecas)
public class StandardErrorDTO {

    private LocalDateTime timestamp; // Data e hora do erro
    private Integer status; // Código de status HTTP (ex: 404, 500)
    private String error; // Descrição do erro (ex: "Not Found", "Internal Server Error")
    private String message; // Mensagem de erro detalhada (ex: "User not found with id 123")
    private String path; // Caminho da requisição que causou o erro (ex: "/users/123")

}