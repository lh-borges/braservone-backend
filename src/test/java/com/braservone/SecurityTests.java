package com.braservone; // Ajuste o pacote se necessário

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.braservone.DTO.UpdateUserDTO;
import com.braservone.controllers.UserController;
import com.braservone.security.WebSecurityConfig;
import com.braservone.security.jwt.AuthEntryPointJwt;
import com.braservone.security.jwt.AuthTokenFilter;
import com.braservone.security.jwt.JwtUtils;
import com.braservone.security.services.UserDetailsServiceImpl;
import com.braservone.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@Import({WebSecurityConfig.class, AuthTokenFilter.class, AuthEntryPointJwt.class})
class SecurityTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserService userService;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("Security: Deve bloquear acesso anônimo (401)")
    void shouldBlockAnonymousAccess() throws Exception {
        mockMvc.perform(get("/api/user/qualquer-um")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Security: Deve prevenir IDOR (retornar 403 Forbidden)")
    void shouldPreventHorizontalPrivilegeEscalation() throws Exception {
        String attackerUsername = "joao";
        String victimUsername = "maria";

        // USAMOS JSON STRING DIRETO PARA EVITAR ERRO DE SERIALIZAÇÃO DO DTO
        String jsonPayload = """
            {
                "nome": "Hacker Nome Valido",
                "email": "hacker@valid.com"
            }
        """;

        mockMvc.perform(patch("/api/user/{id}", victimUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload) // <--- Enviando string crua garantida
                .with(csrf())
                .with(user(attackerUsername).roles("USER"))) 
            .andDo(print()) // Olhe o console se der erro de novo!
            .andExpect(status().isForbidden()); // Espera 403
            
        verify(userService, never()).updateUser(eq(victimUsername), any(), any());
    }

    @Test
    @DisplayName("Security: Deve validar payload gigante (400)")
    void shouldRejectMassivePayload() throws Exception {
        String massiveString = "A".repeat(200); // 200 chars > max 120
        
        UpdateUserDTO overflowDto = new UpdateUserDTO();
        overflowDto.setNome(massiveString); 
        overflowDto.setEmail("test@mail.com");

        mockMvc.perform(patch("/api/user/meu-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overflowDto))
                .with(csrf())
                .with(user("admin")))
            .andDo(print())
            .andExpect(status().isBadRequest()); // Esperamos 400
    }
}