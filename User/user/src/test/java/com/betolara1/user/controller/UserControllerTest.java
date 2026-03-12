package com.betolara1.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.betolara1.user.model.User;
import com.betolara1.user.service.UserService;
import com.betolara1.user.dto.request.UpdateUserRequest;
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

    @Test
    void testGetAll_Admin_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setName("Test User");
        user.setEmail("test@example.com");

        Page<User> page = new PageImpl<>(Collections.singletonList(user));

        when(userService.findAllUsers(0, 10)).thenReturn(page);

        ResponseEntity<Page<UserDTO>> response = userController.getAll(0, 10, "ADMIN", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<UserDTO> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.getTotalElements());
        assertEquals(1, body.getContent().size());
    }

    @Test
    void testGetAll_User_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Page<User> page = new PageImpl<>(Collections.singletonList(user));

        when(userService.findById(1L, 0, 10)).thenReturn(page);

        ResponseEntity<Page<UserDTO>> response = userController.getAll(0, 10, "USER", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<UserDTO> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.getTotalElements());
    }

    @Test
    void testGetAll_Empty() {
        Page<User> page = new PageImpl<>(Collections.emptyList());

        when(userService.findAllUsers(0, 10)).thenReturn(page);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.getAll(0, 10, "ADMIN", 1L);
        });

        assertTrue(exception.getMessage().contains("Nenhum usuário cadastrado"));
    }

    @Test
    void testDeleteUser_Success() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<String> response = userController.deleteUser(1L, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        String body = response.getBody();
        assertEquals("Usuário deletado com sucesso", body);
    }

    @Test
    void testDeleteUser_Forbidden() {
        ResponseEntity<String> response = userController.deleteUser(1L, "USER");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateUser_Success() {
        UpdateUserRequest updatedData = new UpdateUserRequest();
        updatedData.setName("New Name");
        updatedData.setEmail("test@example.com");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("New Name");
        updatedUser.setEmail("test@example.com");

        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class))).thenReturn(updatedUser);

        ResponseEntity<UserDTO> response = userController.updateUser(1L, updatedData, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("New Name", body.getName());
    }

    @Test
    void testUpdateUser_Forbidden() {
        UpdateUserRequest updatedData = new UpdateUserRequest();
        
        ResponseEntity<UserDTO> response = userController.updateUser(1L, updatedData, 2L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
