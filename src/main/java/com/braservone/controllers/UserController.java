// src/main/java/com/projetopetroleo/controllers/UserController.java
package com.braservone.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.braservone.DTO.UpdateUserDTO;
import com.braservone.models.User;
import com.braservone.service.UserService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        return ResponseEntity.ok(user);
    }


    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> patchUser(
            @PathVariable("id") String id,                  // ajuste o tipo para bater com seu ID real (String/Long/UUID)
            @Valid @RequestBody UpdateUserDTO dto) {        // @Valid ativa as constraints do DTO

        Optional<User> updated = userService.updateUser(id, dto.getNome(), dto.getEmail());
        
        return updated.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Criação de usuário (o seu método anterior não tinha mapeamento e usava @PathParam indevido)
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        Optional<User> newUser = this.userService.addUser(user);
        return newUser.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Não foi possível fazer o cadastro"));
    }
    
    /**
     * @param body
     * @param auth
     * @return
     */
    @PatchMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> patchChangePassword(
        @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
        @RequestBody Map<String, String> body) {

        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        userService.changePassword(oldPassword, newPassword);
        return ResponseEntity.noContent().build(); // 204
    }
}
