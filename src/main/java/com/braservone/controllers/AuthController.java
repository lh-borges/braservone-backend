package com.braservone.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.CookieValue; // IMPORTANTE
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.braservone.enums.ERole;
import com.braservone.models.Account;
import com.braservone.models.User;
import com.braservone.payload.request.LoginRequest;
import com.braservone.payload.request.SignupRequest;
import com.braservone.payload.response.JwtResponse;
import com.braservone.payload.response.MessageResponse;
import com.braservone.repository.AccountRepository;
import com.braservone.repository.RoleRepository;
import com.braservone.repository.UserRepository;
import com.braservone.security.jwt.JwtUtils;
import com.braservone.security.services.UserDetailsImpl;
import com.braservone.services.AccountService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String ACCESS_COOKIE  = "access_token";
    private static final String REFRESH_COOKIE = "refresh_token";

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;
    private final AccountService accountService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          AccountRepository accountRepository,
                          RoleRepository roleRepository,
                          JwtUtils jwtUtils,
                          AccountService accountService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.jwtUtils = jwtUtils;
        this.accountService = accountService;
    }

    // =========================================
    // LOGIN
    // =========================================
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                              HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 1) Gera tokens
        String accessToken  = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        // 2) Cookies HttpOnly
        addAccessCookie(response, accessToken);
        addRefreshCookie(response, refreshToken);

        // 3) Pega o principal
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(
            accessToken,
            refreshToken,
            principal.getUsername(),
            principal.getEmail(),
            roles
        ));
    }

    // =========================================
    // ME (Info do Usuário Logado)
    // =========================================
    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetailsImpl principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(new MessageResponse("Unauthorized"));
        }

        List<String> roles = principal.getAuthorities().stream()
            .map(a -> a.getAuthority())
            .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(
            "", // sem token no body (já está no cookie/header)
            principal.getUsername(),
            principal.getEmail(),
            roles
        ));
    }

    // =========================================
    // REFRESH TOKEN (Otimizado com @CookieValue)
    // =========================================
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            // Spring extrai automaticamente o cookie. required=false para tratarmos o nulo manualmente com 401.
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken, 
            HttpServletResponse response) {

        if (refreshToken == null || !jwtUtils.validateJwtToken(refreshToken)) {
            clearAccessCookie(response);
            clearRefreshCookie(response);
            return ResponseEntity.status(401).body(new MessageResponse("Invalid or missing refresh token"));
        }

        String username = jwtUtils.getUserNameFromJwtToken(refreshToken);

        // Gera novo access token e atualiza cookie
        String newAccess = jwtUtils.generateAccessTokenFromUsername(username);
        addAccessCookie(response, newAccess);

        // Carrega a Account para devolver dados
        Account acc = accountRepository.findByUsername(username).orElse(null);

        if (acc == null || acc.getUser() == null) {
            clearAccessCookie(response);
            clearRefreshCookie(response);
            return ResponseEntity.status(401).body(new MessageResponse("Account not found"));
        }

        String email = acc.getUser().getEmail();
        List<String> roles = acc.getRoles().stream()
            .map(r -> r.getName().name())
            .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(
            "", 
            username,
            email,
            roles
        ));
    }

    // =========================================
    // SIGNUP
    // =========================================
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsById(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // 1) Cria perfil (User)
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setNome(Optional.ofNullable(signUpRequest.getNome()).orElse(signUpRequest.getUsername()));
        user.setCpf(Optional.ofNullable(signUpRequest.getCpf()).orElse("00000000000"));
        
        user.setRoles(new HashSet<>());
        userRepository.save(user);

        // 2) Cria credenciais (Account)
        Set<String> strRoles = signUpRequest.getRole();
        Set<ERole> roleNames = new HashSet<>();
        
        if (strRoles == null || strRoles.isEmpty()) {
            roleNames.add(ERole.ROLE_USER);
        } else {
            for (String r : strRoles) {
                switch (r.toLowerCase()) {
                    case "admin" -> roleNames.add(ERole.ROLE_ADMIN);
                    case "master" -> roleNames.add(ERole.ROLE_MASTER);
                    default -> roleNames.add(ERole.ROLE_USER);
                }
            }
        }

        accountService.enableLogin(
            signUpRequest.getUsername(),
            signUpRequest.getPassword(),
            roleNames
        );

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    // =======================
    // Helpers de Cookie
    // =======================
    private void addAccessCookie(HttpServletResponse response, String token) {
        ResponseCookie accessCookie = ResponseCookie.from(ACCESS_COOKIE, token)
            .httpOnly(true)
            .secure(true)     // Em produção (HTTPS) deve ser true.
            .sameSite("Lax")  // Necessário para cookies cross-site/navegação
            .path("/")
            .maxAge(15 * 60)  // 15 minutos
            .build();
        response.addHeader("Set-Cookie", accessCookie.toString());
    }

    private void addRefreshCookie(HttpServletResponse response, String token) {
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE, token)
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .path("/api/auth") // Refresh só é enviado para rotas de auth
            .maxAge(30L * 24 * 60 * 60) // 30 dias
            .build();
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    private void clearAccessCookie(HttpServletResponse response) {
        ResponseCookie clear = ResponseCookie.from(ACCESS_COOKIE, "")
            .httpOnly(true).secure(true).sameSite("Lax").path("/").maxAge(0).build();
        response.addHeader("Set-Cookie", clear.toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie clear = ResponseCookie.from(REFRESH_COOKIE, "")
            .httpOnly(true).secure(true).sameSite("Lax").path("/api/auth").maxAge(0).build();
        response.addHeader("Set-Cookie", clear.toString());
    }
}