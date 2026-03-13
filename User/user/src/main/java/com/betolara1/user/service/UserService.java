package com.betolara1.user.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.betolara1.user.model.User;
import com.betolara1.user.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.betolara1.user.exception.NotFoundException;
import com.betolara1.user.dto.request.RegisterRequest;
import com.betolara1.user.dto.request.UpdateUserRequest;
import com.betolara1.user.exception.ConflictException;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository; // Injeção de dependência via construtor, por isso usa o final
    private final PasswordEncoder passwordEncoder; // Injeção de dependência via construtor, por isso usa o final

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Adicionado endpoint no user para listar todos os usuarios, com paginação
    public Page<User> findAllUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Usuario não encontrado com username: " + username));
    }

    public Page<User> findById(Long id, int page, int size) {
        return userRepository.findById(id, PageRequest.of(page, size));
    }

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ConflictException("Usuário já cadastrado.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(encodedPassword);
        newUser.setRole(User.Role.USER);
        newUser.setAddress(request.getAddress());
        newUser.setEmail(request.getEmail());
        newUser.setName(request.getName());
        newUser.setPhone(request.getPhone());
        return userRepository.save(newUser);
    }

    @Transactional
    public User updateUser(Long id, UpdateUserRequest updatedUser) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuario não encontrado com ID: " + id));

        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPhone() != null) {
            user.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getAddress() != null) {
            user.setAddress(updatedUser.getAddress());
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario não encontrado com ID: " + id));
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + username));

        // Pega o papel do usuário ou define ROLE_USER se estiver nulo
        String userRole = user.getRole() != null ? user.getRole().name() : "USER";

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(userRole)));
    }
}
