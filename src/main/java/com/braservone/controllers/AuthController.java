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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// REMOVIDO: import com.braservone.DTO.EmpresaDTO;
import com.braservone.enums.ERole;
import com.braservone.models.Account;
// REMOVIDO: import com.braservone.models.Empresa;
import com.braservone.models.User;
import com.braservone.payload.request.LoginRequest;
// IMPORT FALTANTE:
import com.braservone.payload.request.SignupRequest;
import com.braservone.payload.response.JwtResponse;
import com.braservone.payload.response.MessageResponse;
import com.braservone.repository.AccountRepository;
import com.braservone.repository.RoleRepository;
import com.braservone.repository.UserRepository;
import com.braservone.security.jwt.JwtUtils;
import com.braservone.security.services.UserDetailsImpl;
import com.braservone.service.AccountService;
// REMOVIDO: import com.braservone.service.EmpresaService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private static final String ACCESS_COOKIE  = "access_token";
  private static final String REFRESH_COOKIE = "refresh_token";

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;           // ainda usado no signup (perfil)
  private final AccountRepository accountRepository;     // usado no refresh
  private final RoleRepository roleRepository;
  // REMOVIDO: private final EmpresaService empService;
  private final JwtUtils jwtUtils;
  private final AccountService accountService;           // para criar habilitar login no signup

  public AuthController(AuthenticationManager authenticationManager,
                        UserRepository userRepository,
                        AccountRepository accountRepository,
                        RoleRepository roleRepository,
                        // REMOVIDO: EmpresaService empService,
                        JwtUtils jwtUtils,
                        AccountService accountService) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.accountRepository = accountRepository;
    this.roleRepository = roleRepository;
    // REMOVIDO: this.empService = empService;
    this.jwtUtils = jwtUtils;
    this.accountService = accountService;
  }

  // =========================================
  // LOGIN (gera access e refresh em cookies)
  // =========================================
  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                            HttpServletResponse response) {

      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);

      // 1) Gera tokens
      String accessToken  = jwtUtils.generateJwtToken(authentication);      // expira em ~15 min
      String refreshToken = jwtUtils.generateRefreshToken(authentication);  // expira em ~30 dias

      // (opcional) 2) Cookies HttpOnly – pode manter, se quiser autenticação dupla
      addAccessCookie(response, accessToken);
      addRefreshCookie(response, refreshToken);

      // 3) Pega o principal
      UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

      List<String> roles = principal.getAuthorities().stream()
              .map(a -> a.getAuthority())
              .collect(Collectors.toList());


      // 4) Retorna também os tokens no corpo
      return ResponseEntity.ok(new JwtResponse(
          accessToken,       // ✅ agora o token vem no body
          refreshToken,      // ✅ inclui o refresh também
          principal.getUsername(),
          principal.getEmail(),
          // REMOVIDO: empresaDTO,
          roles
      ));
  }

  // =========================================
  // /api/auth/me
  // =========================================
  @GetMapping("/me")
  public ResponseEntity<?> me(@AuthenticationPrincipal UserDetailsImpl principal) {
    if (principal == null) {
      return ResponseEntity.status(401).body(new MessageResponse("Unauthorized"));
    }

    List<String> roles = principal.getAuthorities().stream()
        .map(a -> a.getAuthority())
        .collect(Collectors.toList());

    // REMOVIDO: EmpresaDTO empresaDTO = empService.toDTO(principal.getEmpresa());

    return ResponseEntity.ok(new JwtResponse(
        "", // sem token no body
        principal.getUsername(),
        principal.getEmail(),
        // REMOVIDO: empresaDTO,
        roles
    ));
  }

  // =========================================
  // /api/auth/refresh
  // =========================================
  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = getCookieValue(request, REFRESH_COOKIE);
    if (refreshToken == null || !jwtUtils.validateJwtToken(refreshToken)) {
      clearAccessCookie(response);
      clearRefreshCookie(response);
      return ResponseEntity.status(401).body(new MessageResponse("Invalid or missing refresh token"));
    }

    String username = jwtUtils.getUserNameFromJwtToken(refreshToken);

    // Gera novo access token e atualiza cookie
    String newAccess = jwtUtils.generateAccessTokenFromUsername(username);
    addAccessCookie(response, newAccess);

    // Carrega a Account para devolver dados (email, roles)
    Account acc = accountRepository.findByUsername(username)
        .orElse(null);

    if (acc == null || acc.getUser() == null) {
      // conta não existe mais — invalida refresh também
      clearAccessCookie(response);
      clearRefreshCookie(response);
      return ResponseEntity.status(401).body(new MessageResponse("Account not found"));
    }

    String email = acc.getUser().getEmail();
    List<String> roles = acc.getRoles().stream()
        .map(r -> r.getName().name())
        .collect(Collectors.toList());

    // REMOVIDO: EmpresaDTO empresaDTO = empService.toDTO(acc.getUser().getEmpresa());

    return ResponseEntity.ok(new JwtResponse(
        "", // token fica no cookie
        username,
        email,
        // REMOVIDO: empresaDTO,
        roles
    ));
  }

  // =========================================
  // SIGNUP — cria User (perfil) e Account (login)
  // =========================================
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    // Unicidade por username e email
    if (userRepository.existsById(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    // 1) Cria perfil (User) — ajuste aqui conforme seus campos obrigatórios (nome, cpf, empresa etc.)
    User user = new User();
    user.setUsername(signUpRequest.getUsername());
    user.setEmail(signUpRequest.getEmail());
    user.setNome(Optional.ofNullable(signUpRequest.getNome()).orElse(signUpRequest.getUsername())); // fallback
    user.setCpf(Optional.ofNullable(signUpRequest.getCpf()).orElse("00000000000"));

    // REMOVIDO: Lógica para buscar/setar Empresa:
    /*
    if (signUpRequest.getEmpresaId() != null) {
      Empresa emp = empService.findById(signUpRequest.getEmpresaId())
          .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
      user.setEmpresa(emp);
    } else {
      Empresa empDefault = empService.getDefaultEmpresa()
          .orElseThrow(() -> new RuntimeException("Empresa padrão não configurada"));
      user.setEmpresa(empDefault);
    }
    */
    // Se o campo 'empresa' era obrigatório na entidade User, você DEVE removê-lo de lá.

    // (Opcional) roles organizacionais do perfil (user_roles). Pode deixar vazio.
    user.setRoles(new HashSet<>());
    userRepository.save(user);

    // 2) Cria credenciais (Account) — habilita login
    //    Resolve roles de login a partir do que vier no request
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
        signUpRequest.getPassword(),   // senha crua aqui; service faz o encode
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
        .secure(true)     // em DEV local sem HTTPS, pode usar false
        .sameSite("Lax")  // se front e back forem domínios diferentes, use "None"
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
        .path("/api/auth")              // restringe o envio do refresh a rotas de auth
        .maxAge(30L * 24 * 60 * 60)     // 30 dias
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

  private String getCookieValue(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) return null;
    for (Cookie c : cookies) {
      if (name.equals(c.getName())) {
        return c.getValue();
      }
    }
    return null;
  }
}
