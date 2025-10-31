package com.braservone.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import com.braservone.models.User;
import com.braservone.repository.UserRepository;
import com.braservone.security.jwt.CurrentUser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Service
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final CurrentUser currentUser;
    private final AccountService accountService;

    public UserService(UserRepository userRepository,
                       CurrentUser currentUser,
                       AccountService accountService) {
        this.userRepository = userRepository;
        this.currentUser = currentUser;
        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findById(username);
    }

    @Transactional
    public Optional<User> addUser(User user) {
        if (userRepository.existsById(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username já existe");
        }
        return Optional.of(userRepository.save(user));
    }

    @Transactional
    public Optional<User> updateUser(String idUser, String nome, String email) {
        return userRepository.findById(idUser)
                .map(user -> {
                    user.updateUser(nome, email);
                    return userRepository.save(user);
                });
    }

    @Transactional(readOnly = true)
    public boolean verUserUsername(String username) {
        return userRepository.existsById(username);
    }

    /**
     * Mantido pra compatibilidade: troca a senha do usuário logado.
     * A lógica pesada fica no AccountService.
     */
    @Transactional
    public boolean changePassword(
            @NotBlank String oldPassword,
            @NotBlank @Size(min = 6, max = 128) String newPassword) {

        final String username = currentUser.username();
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não autenticado");
        }

        accountService.changeMyPassword(oldPassword, newPassword);
        return true;
    }
}
