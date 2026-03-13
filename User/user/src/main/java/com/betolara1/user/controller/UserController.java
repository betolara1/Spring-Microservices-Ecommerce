package com.betolara1.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;

import com.betolara1.user.model.User;
import com.betolara1.user.service.UserService;
import com.betolara1.user.dto.request.UpdateUserRequest;
import com.betolara1.user.dto.response.UserDTO;
import com.betolara1.user.exception.NotFoundException;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<Page<UserDTO>> getAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") Long userId){

        if(role.equals("ADMIN")){
            Page<UserDTO> list = userService.findAllUsers(page, size).map(UserDTO::new);

            if (list.isEmpty()) {
                throw new NotFoundException("Nenhum usuário cadastrado.");
            }

            return ResponseEntity.ok(list);
        }
        else if(role.equals("USER")){
            Page<UserDTO> list = userService.findById(userId, page, size).map(UserDTO::new);

            if (list.isEmpty()) {
                throw new NotFoundException("Nenhum usuário cadastrado.");
            }

            return ResponseEntity.ok(list);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, @RequestHeader("X-User-Role") String role) {
        if(role.equals("ADMIN")){
            userService.deleteUser(id);
            return ResponseEntity.ok("Usuário deletado com sucesso");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest updatedUser, @RequestHeader("X-User-Id") Long userId) {
        if(userId.equals(id)){
            User userDB = userService.updateUser(id, updatedUser);
            UserDTO userDTOUpdated = new UserDTO(userDB);

            return ResponseEntity.ok(userDTOUpdated);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
