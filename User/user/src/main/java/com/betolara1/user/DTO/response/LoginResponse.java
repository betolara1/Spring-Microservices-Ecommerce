package com.betolara1.user.dto.response;

// Neste caso, o LoginDTO é usado para retornar os dados de login do usuário, como o token JWT e o username. 
// Ele é uma forma segura e clara de enviar apenas as informações necessárias para o cliente após o login bem-sucedido.
// A sintaxe de Record do Java é uma forma concisa de criar classes imutáveis que são principalmente usadas para transportar dados. 
// Ela gera automaticamente os métodos necessários, como construtores e getters, o que reduz a quantidade de código boilerplate.
// O LoginDTO é usado no AuthController para retornar o token JWT e o username do usuário após um login bem-sucedido. 
// Ele é anotado com @RequestBody, o que significa que o Spring vai mapear automaticamente os dados JSON da resposta para os campos do DTO.
public record LoginResponse(String token, String username) {}
