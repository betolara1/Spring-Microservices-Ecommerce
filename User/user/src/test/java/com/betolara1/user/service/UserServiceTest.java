package com.betolara1.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.betolara1.user.model.User;
import com.betolara1.user.repository.UserRepository;
import com.betolara1.user.dto.request.RegisterRequest;
import com.betolara1.user.dto.request.UpdateUserRequest;
import com.betolara1.user.exception.NotFoundException;
import com.betolara1.user.exception.ConflictException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testFindByUsername_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.findByUsername("nonexistent");
        });

        assertTrue(exception.getMessage().contains("Usuario não encontrado"));
    }

    @Test
    void testFindById_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Page<User> page = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findById(eq(1L), any(PageRequest.class))).thenReturn(page);

        Page<User> result = userService.findById(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
    }

    @Test
    void testFindById_Empty() {
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList());
        when(userRepository.findById(eq(999L), any(PageRequest.class))).thenReturn(emptyPage);

        Page<User> result = userService.findById(999L, 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void testRegisterUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setName("Test");
        request.setEmail("test@test.com");
        request.setPhone("123456");
        request.setAddress("Test Address");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User result = userService.registerUser(request);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("encodedpassword", result.getPassword());
        assertEquals(User.Role.USER, result.getRole());
    }

    @Test
    void testRegisterUser_Conflict() {
        User existingUser = new User();
        existingUser.setUsername("existing");

        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing");
        request.setPassword("password123");
        request.setName("Test");
        request.setEmail("test@test.com");
        request.setPhone("123456");
        request.setAddress("Test Address");

        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(existingUser));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            userService.registerUser(request);
        });

        assertTrue(exception.getMessage().contains("Usuário já cadastrado"));
    }

    @Test
    void testUpdateUser_Success() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("testuser");
        existingUser.setName("Old Name");

        UpdateUserRequest updatedData = new UpdateUserRequest();
        updatedData.setName("New Name");
        updatedData.setEmail("test@example.com");
        updatedData.setPhone("987654");
        updatedData.setAddress("New Address");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User result = userService.updateUser(1L, updatedData);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("987654", result.getPhone());
        assertEquals("New Address", result.getAddress());
    }

    @Test
    void testUpdateUser_NotFound() {
        UpdateUserRequest updatedData = new UpdateUserRequest();
        updatedData.setName("New Name");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.updateUser(999L, updatedData);
        });

        assertTrue(exception.getMessage().contains("Usuario não encontrado"));
    }

    @Test
    void testDeleteUser_Success() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.deleteUser(999L);
        });

        assertTrue(exception.getMessage().contains("Usuario não encontrado"));
    }

    @Test
    void testLoadUserByUsername_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedpassword");
        user.setRole(User.Role.USER);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        org.springframework.security.core.userdetails.UserDetails userDetails = userService
                .loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedpassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("USER")));
    }

}
