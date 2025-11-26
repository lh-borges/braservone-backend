package com.braservone;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import com.braservone.controllers.AuthController;
import com.braservone.payload.request.LoginRequest;
import com.braservone.payload.request.SignupRequest;
import com.braservone.repository.AccountRepository;
import com.braservone.repository.RoleRepository;
import com.braservone.repository.UserRepository;
import com.braservone.security.jwt.AuthEntryPointJwt;
import com.braservone.security.jwt.AuthTokenFilter;
import com.braservone.security.jwt.JwtUtils;
import com.braservone.security.services.UserDetailsImpl;
import com.braservone.security.services.UserDetailsServiceImpl;
import com.braservone.services.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    // --- DEPENDÊNCIAS DO AUTHCONTROLLER ---
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private UserRepository userRepository;
    @MockBean private AccountRepository accountRepository;
    @MockBean private RoleRepository roleRepository;
    @MockBean private JwtUtils jwtUtils; 
    @MockBean private AccountService accountService;

    // --- DEPENDÊNCIAS DE INFRAESTRUTURA DE SEGURANÇA ---
    // Necessárias para o contexto do Spring Security subir sem erro de Bean Missing
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private AuthEntryPointJwt authEntryPointJwt;
    @MockBean private AuthTokenFilter authTokenFilter;

    @Test
    @DisplayName("Login: Deve autenticar e retornar Tokens (Body + Cookies)")
    void shouldAuthenticateUser() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // Cria o UserDetailsImpl usando o construtor de compatibilidade que criamos
        UserDetailsImpl userDetails = new UserDetailsImpl(
            "1", // ID (ignorado pelo construtor interno, mas necessário p/ assinatura)
            "admin", 
            "admin@mail.com", 
            "pass", 
            authorities 
        );
        
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // Mocks do comportamento de sucesso
        given(authenticationManager.authenticate(any())).willReturn(auth);
        given(jwtUtils.generateJwtToken(auth)).willReturn("fake-jwt-token");
        given(jwtUtils.generateRefreshToken(auth)).willReturn("fake-refresh-token");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
                .with(csrf())) // CSRF é obrigatório para POST em testes unitários
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("fake-jwt-token"))
            .andExpect(jsonPath("$.refreshToken").value("fake-refresh-token"))
            .andExpect(jsonPath("$.username").value("admin"))
            // Verifica se os Cookies HttpOnly foram setados
            .andExpect(cookie().exists("access_token"))
            .andExpect(cookie().exists("refresh_token"))
            .andExpect(cookie().value("access_token", "fake-jwt-token"));
    }

    @Test
    @DisplayName("Signup: Deve registrar usuário com sucesso")
    void shouldRegisterUser() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("novo_user");
        signupRequest.setEmail("novo@mail.com");
        signupRequest.setPassword("123456");
        signupRequest.setRole(Set.of("user")); 

        // Mock: não existe conflito
        given(userRepository.existsById("novo_user")).willReturn(false);
        given(userRepository.existsByEmail("novo@mail.com")).willReturn(false);
        
        // Mock do AccountService (método void)
        doNothing().when(accountService).enableLogin(any(), any(), any());

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    @DisplayName("Signup: Deve retornar 400 se Username já existe")
    void shouldFailRegisterIfUsernameExists() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("existente");
        signupRequest.setEmail("teste@mail.com");
        signupRequest.setPassword("123");

        // Simula conflito
        given(userRepository.existsById("existente")).willReturn(true);

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest))
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Error: Username is already taken!"));
    }
}