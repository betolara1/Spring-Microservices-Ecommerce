package com.betolara1.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.betolara1.user.model.User;
import com.betolara1.user.security.JwtUtil;
import com.betolara1.user.service.UserService;
import com.betolara1.user.dto.request.LoginRequest;
import com.betolara1.user.dto.response.LoginResponse;
import com.betolara1.user.dto.response.UserDTO;
import com.betolara1.user.exception.NotFoundException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @InjectMocks
    private AuthController authController;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    void testLogin_Success(){
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("testpassword");

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("testpassword");

        when(userService.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("testpassword", user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken("testuser")).thenReturn("testtoken");

        ResponseEntity<LoginResponse> response = (ResponseEntity<LoginResponse>) authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testtoken", response.getBody().token());
        assertEquals("testuser", response.getBody().username());
    }

    @Test
    void testLogin_InvalidCredentials() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("testpassword");

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(userService.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", user.getPassword())).thenReturn(false);

        assertTrue(authController.login(request) instanceof ResponseEntity);
    }

    @Test
    void testListAll_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setName("Test User");
        user.setEmail("test@example.com");

        Page<User> page = new PageImpl<>(Collections.singletonList(user));

        when(userService.findAllUsers(0, 10)).thenReturn(page);

        ResponseEntity<Page<UserDTO>> response = userController.listAll(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void testListAll_Empty() {
        Page<User> page = new PageImpl<>(Collections.emptyList());

        when(userService.findAllUsers(0, 10)).thenReturn(page);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.listAll(0, 10);
        });

        assertTrue(exception.getMessage().contains("Nenhum usuário cadastrado"));
    }

    @Test
    void testGetUser_ById_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setName("Test User");

        when(userService.findById(1L)).thenReturn(user);

        ResponseEntity<UserDTO> response = userController.getUser("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testGetUser_ByUsername_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setName("Test User");

        when(userService.findByUsername("testuser")).thenReturn(user);

        ResponseEntity<UserDTO> response = userController.getUser("testuser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    void testGetUser_NotFound() {
        when(userService.findById(999L)).thenThrow(new NotFoundException("Usuário não encontrado"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.getUser("999");
        });

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
    }

    @Test
    void testDeleteUser_Success() {
        User user = new User();
        user.setId(1L);

        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<String> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuário deletado com sucesso", response.getBody());
    }

    @Test
    void testDeleteUser_NotFound() {
        doThrow(new NotFoundException("Usuário não encontrado")).when(userService).deleteUser(999L);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.deleteUser(999L);
        });

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
    }

    @Test
    void testUpdateUser_Success() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("testuser");
        existingUser.setName("Old Name");

        User updatedData = new User();
        updatedData.setName("New Name");
        updatedData.setEmail("test@example.com");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("testuser");
        updatedUser.setName("New Name");
        updatedUser.setEmail("test@example.com");

        when(userService.updateUser(1L, updatedData)).thenReturn(updatedUser);

        ResponseEntity<UserDTO> response = userController.updateUser(1L, updatedData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Name", response.getBody().getName());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    void testUpdateUser_NotFound() {
        User updatedData = new User();
        updatedData.setName("New Name");

        when(userService.updateUser(999L, updatedData))
                .thenThrow(new NotFoundException("Usuário não encontrado"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.updateUser(999L, updatedData);
        });

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
    }
}
