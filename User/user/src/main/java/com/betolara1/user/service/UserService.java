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
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario não encontrado com username: " + username));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario não encontrado com ID: " + id));
    }

    @Transactional
    public User saveUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ConflictException("Usuário já cadastrado.");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);
        newUser.setRole("USER"); // Define um papel padrão para novos usuários
        return userRepository.save(newUser);
    }

    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario não encontrado com ID: " + id));

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPhone(updatedUser.getPhone());
        user.setAddress(updatedUser.getAddress());

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
        String userRole = user.getRole() != null ? user.getRole() : "USER";

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(userRole)));
    }
}
