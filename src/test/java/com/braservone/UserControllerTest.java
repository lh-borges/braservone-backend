package com.braservone;



import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.braservone.DTO.UpdateUserDTO;
import com.braservone.controllers.UserController;
import com.braservone.models.User;
import com.braservone.security.jwt.AuthEntryPointJwt;
import com.braservone.security.jwt.JwtUtils;
import com.braservone.security.services.UserDetailsServiceImpl;
import com.braservone.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired 
    private MockMvc mockMvc;
    
    @Autowired 
    private ObjectMapper objectMapper;

    // --- DEPENDÊNCIA DO CONTROLLER ---
    @MockBean 
    private UserService userService;

    // --- DEPENDÊNCIAS DA CAMADA DE SEGURANÇA (NECESSÁRIAS PARA O CONTEXTO SUBIR) ---
    // O erro anterior ocorria porque o AuthTokenFilter precisa do JwtUtils e do UserDetailsService
    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;

    // --- TESTES ---

    // 1. GET USER
    @Test
    @DisplayName("GET: Deve retornar usuário se encontrado")
    void shouldGetUserFound() throws Exception {
        User user = new User();
        user.setUsername("jdoe");
        user.setEmail("jdoe@mail.com");

        given(userService.getUserByUsername("jdoe")).willReturn(Optional.of(user));

        mockMvc.perform(get("/api/user/{id}", "jdoe")
                .with(user("admin"))) // Simula autenticação
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("jdoe"));
    }

    @Test
    @DisplayName("GET: Deve retornar 404 se usuário não existe")
    void shouldReturn404IfUserNotFound() throws Exception {
        given(userService.getUserByUsername("ghost")).willReturn(Optional.empty());

        mockMvc.perform(get("/api/user/{id}", "ghost")
                .with(user("admin")))
            .andExpect(status().isNotFound());
    }

    // 2. UPDATE USER (PATCH)
    @Test
    @DisplayName("PATCH: Deve atualizar usuário com sucesso")
    void shouldUpdateUser() throws Exception {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setNome("Novo Nome");
        dto.setEmail("novo@mail.com");

        User updatedUser = new User();
        updatedUser.setUsername("jdoe");
        updatedUser.setNome("Novo Nome");

        given(userService.updateUser(eq("jdoe"), eq("Novo Nome"), eq("novo@mail.com")))
            .willReturn(Optional.of(updatedUser));

        mockMvc.perform(patch("/api/user/{id}", "jdoe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(csrf()) // Necessário para métodos de escrita
                .with(user("admin")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Novo Nome"));
    }

    // 3. CHANGE PASSWORD
    @Test
    @DisplayName("PATCH PASSWORD: Deve trocar senha (204 No Content)")
    void shouldChangePassword() throws Exception {
        Map<String, String> body = Map.of(
            "oldPassword", "old123",
            "newPassword", "new123"
        );

        // Nenhuma exceção lançada pelo service = sucesso
        
        mockMvc.perform(patch("/api/user/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .with(csrf())
                .with(user("currentUser").password("pass")))
            .andExpect(status().isNoContent()); // 204
    }

    @Test
    @DisplayName("PATCH PASSWORD: Deve retornar erro (status do Service) se falhar")
    void shouldReturnErrorIfChangePasswordFails() throws Exception {
        // Simula o Service lançando exceção (ex: senha antiga errada)
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha incorreta"))
            .when(userService).changePassword("errada", "nova123");

        Map<String, String> body = Map.of(
            "oldPassword", "errada",
            "newPassword", "nova123"
        );

        mockMvc.perform(patch("/api/user/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .with(csrf())
                .with(user("currentUser")))
            .andExpect(status().isBadRequest());
    }
}