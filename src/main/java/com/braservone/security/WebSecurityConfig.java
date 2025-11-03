package com.braservone.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.braservone.security.jwt.AuthEntryPointJwt;
import com.braservone.security.jwt.AuthTokenFilter;
import com.braservone.security.services.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

  private final UserDetailsServiceImpl userDetailsService;
  private final AuthEntryPointJwt unauthorizedHandler;
  private final AuthTokenFilter authTokenFilter;

  public WebSecurityConfig(UserDetailsServiceImpl userDetailsService,
                           AuthEntryPointJwt unauthorizedHandler,
                           AuthTokenFilter authTokenFilter) {
    this.userDetailsService = userDetailsService;
    this.unauthorizedHandler = unauthorizedHandler;
    this.authTokenFilter = authTokenFilter;
  }

  /* ===== Password & Auth ===== */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  /* ===== CORS ===== */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();

    // Origens EXATAS do front (prod + dev). HTTPS no Cloud Run.
    cfg.setAllowedOrigins(List.of(
        "https://braserv-front-786579739769.southamerica-east1.run.app",
        "http://localhost:4200",
        "http://127.0.0.1:4200"
    ));

    // Métodos usados pelo front (inclui preflight)
    cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

    // Headers que o front envia
    cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));

    // JWT via Authorization header -> sem cookies -> deixe FALSE
    cfg.setAllowCredentials(false);

    // Cache do preflight no navegador (1h)
    cfg.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }

  /* ===== Security ===== */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .cors(c -> c.configurationSource(corsConfigurationSource()))
      .csrf(csrf -> csrf.disable())
      .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
          // Preflight liberado
          .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

          // Endpoints públicos de auth
          .requestMatchers("/api/auth/login", "/api/auth/refresh").permitAll()

          // (opcional) health para readiness/liveness
          .requestMatchers("/actuator/health").permitAll()

          // Demais precisam de auth
          .anyRequest().authenticated()
      );

    http.authenticationProvider(authenticationProvider());
    http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
