package com.betolara1.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.betolara1.user.security.JwtUtil;
import com.betolara1.user.dto.request.LoginRequest;
import com.betolara1.user.dto.request.RegisterRequest;
import com.betolara1.user.dto.response.LoginResponse;
import com.betolara1.user.dto.response.UserDTO;
import com.betolara1.user.model.User;
import com.betolara1.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    // O RegisterRequest é um DTO que contém os campos necessários para o registro,
    // como username e password.
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // O método saveUser já cuida de codificar a senha, então passamos a senha crua
        // mesmo que o DTO tenha a senha crua, pois o serviço vai lidar com isso de
        // forma segura
        User user = userService.saveUser(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new UserDTO(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.findByUsername(request.getUsername());

        // CONDIÇÃO PARA VER SE O USUARIO EXISTE E A SENHA FOR IGUAL
        // O matches verifica se a senha crua bate com o hash
        // Se o usuário existir e a senha estiver correta, gera o token JWT
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername());

            // Retorna o token JWT e o username do usuário no LoginDTO
            // O LoginDTO é uma forma segura e clara de enviar apenas as informações
            // necessárias para o cliente após o login bem-sucedido
            return ResponseEntity.ok(new LoginResponse(token, user.getUsername()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario ou senha inválidos");
    }
}