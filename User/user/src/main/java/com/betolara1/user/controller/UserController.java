package com.betolara1.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;

import com.betolara1.user.DTO.response.UserDTO;
import com.betolara1.user.model.User;
import com.betolara1.user.service.UserService;
import com.betolara1.user.exception.NotFoundException;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/listAll")
    public ResponseEntity<Page<User>> listAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        Page<User> list = userService.findAllUsers(page, size);

        if (list.isEmpty()) {
            throw new NotFoundException("Nenhum usuário cadastrado.");
        }

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String identifier) {
        User user;

        // Verifica se a String contém apenas dígitos (0-9)
        // Se for o caso, trata como ID do usuário
        if (identifier.matches("\\d+")) { // Verifica se a string contém apenas dígitos (0-9)
            Long id = Long.parseLong(identifier);
            user = userService.findById(id)
                    .orElseThrow(() -> new NotFoundException("Usuário não encontrado com ID: " + id));
        } else {
            // Caso contrário, trata como username
            user = userService.findByUsername(identifier)
                    .orElseThrow(() -> new NotFoundException("Usuário não encontrado com username: " + identifier));
        }

        return ResponseEntity.ok(new UserDTO(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Usuário deletado com sucesso");
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody User user) {
        User userDB = userService.updateUser(id, user);
        UserDTO userDTOUpdated = new UserDTO(userDB);

        return ResponseEntity.ok(userDTOUpdated);
    }
}
