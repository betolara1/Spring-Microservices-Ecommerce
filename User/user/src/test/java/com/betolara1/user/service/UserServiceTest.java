package com.betolara1.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.betolara1.user.model.User;
import com.betolara1.user.repository.UserRepository;
import com.betolara1.user.exception.NotFoundException;
import com.betolara1.user.exception.ConflictException;

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

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.findById(999L);
        });

        assertTrue(exception.getMessage().contains("Usuario não encontrado"));
    }

    @Test
    void testSaveUser_Success() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User result = userService.saveUser("newuser", "password");

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("encodedpassword", result.getPassword());
        assertEquals("USER", result.getRole());
    }

    @Test
    void testSaveUser_Conflict() {
        User existingUser = new User();
        existingUser.setUsername("existing");

        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(existingUser));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            userService.saveUser("existing", "password");
        });

        assertTrue(exception.getMessage().contains("Usuário já cadastrado"));
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

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User result = userService.updateUser(1L, updatedData);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testUpdateUser_NotFound() {
        User updatedData = new User();
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
        user.setRole("USER");

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
