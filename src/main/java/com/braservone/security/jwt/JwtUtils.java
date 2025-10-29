package com.braservone.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import javax.crypto.SecretKey;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.braservone.security.services.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  /* ======= ACCESS TOKEN ======= */
  @Value("${bezkoder.app.jwtSecret}")
  private String jwtSecret;

  @Value("${bezkoder.app.jwtExpirationMs}")
  private long jwtExpirationMs;

  /* ======= REFRESH TOKEN ======= */
  @Value("${bezkoder.app.jwtRefreshSecret:#{null}}")
  private String jwtRefreshSecret;

  @Value("${bezkoder.app.jwtRefreshExpirationMs:1209600000}") // 14 dias
  private long jwtRefreshExpirationMs;

  private SecretKey accessKey;
  private SecretKey refreshKey;

  @PostConstruct
  void initKeys() {
    this.accessKey = keyFrom(jwtSecret);
    String refreshSecretToUse = (jwtRefreshSecret == null || jwtRefreshSecret.isBlank())
        ? jwtSecret
        : jwtRefreshSecret;
    this.refreshKey = keyFrom(refreshSecretToUse);
  }

  private SecretKey keyFrom(String secret) {
    byte[] bytes;
    try {
      bytes = Decoders.BASE64.decode(secret);
    } catch (Exception ignore) {
      bytes = secret.getBytes(StandardCharsets.UTF_8);
    }
    return Keys.hmacShaKeyFor(bytes); // HS256 (>= 32 bytes)
  }

  /* ===================== ACCESS TOKEN ===================== */

  public String generateJwtToken(Authentication authentication) {
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
    return generateTokenFromUsername(userPrincipal.getUsername());
  }

  public String generateTokenFromUsername(String username) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + jwtExpirationMs);

    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(accessKey, SignatureAlgorithm.HS256)
        .compact();
  }

  /** Alias usado pelo seu controller (/refresh). */
  public String generateAccessTokenFromUsername(String username) {
    return generateTokenFromUsername(username);
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(accessKey)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(authToken);
      return true;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }
    return false;
  }

  /* ===================== REFRESH TOKEN ===================== */

  public String generateRefreshToken(Authentication authentication) {
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
    return generateRefreshTokenFromUsername(userPrincipal.getUsername());
  }

  public String generateRefreshTokenFromUsername(String username) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + jwtRefreshExpirationMs);

    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(refreshKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public String getUserNameFromRefreshToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(refreshKey)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean validateRefreshToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
      return true;
    } catch (MalformedJwtException e) {
      logger.error("Invalid REFRESH token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("REFRESH token expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("REFRESH token unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("REFRESH claims empty: {}", e.getMessage());
    }
    return false;
  }

  /* ===================== NOVO: UTILIDADES DE CONTEXTO ===================== */

  /** Authentication atual do contexto (ou null). */
  public Authentication getCurrentAuthentication() {
    return SecurityContextHolder.getContext() != null
        ? SecurityContextHolder.getContext().getAuthentication()
        : null;
  }

  /**
   * Username do usuário autenticado a partir do SecurityContext.
   * Retorna null se não houver auth válida.
   */
  public String getCurrentUsername() {
    Authentication auth = getCurrentAuthentication();
    if (auth == null || !auth.isAuthenticated()) return null;

    Object principal = auth.getPrincipal();
    if (principal instanceof UserDetailsImpl ud) {
      return ud.getUsername();
    }
    // Fallback: alguns providers usam apenas o name
    return auth.getName();
  }

  /**
   * Principal tipado do seu projeto. Útil se você guarda mais infos no UserDetailsImpl.
   * Retorna null se não disponível.
   */
  public UserDetailsImpl getCurrentUserDetails() {
    Authentication auth = getCurrentAuthentication();
    if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetailsImpl ud) {
      return ud;
    }
    return null;
  }

  /**
   * Lê o token Bearer do request atual (quando disponível) e o retorna sem o prefixo.
   * Útil quando você quer validar/extrair claims manualmente.
   */
  public String getCurrentBearerToken() {
    RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
    if (!(attrs instanceof ServletRequestAttributes sra)) return null;

    HttpServletRequest req = sra.getRequest();
    if (req == null) return null;

    String header = req.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }

  /**
   * Username do request atual, tentando primeiro o SecurityContext.
   * Se não houver auth populada (ex.: em algum filtro antes), tenta decodificar do Bearer atual.
   */
  public String resolveUsernameFromContextOrToken() {
    String fromContext = getCurrentUsername();
    if (fromContext != null) return fromContext;

    String token = getCurrentBearerToken();
    if (token == null) return null;

    try {
      return getUserNameFromJwtToken(token);
    } catch (Exception e) {
      logger.debug("Falha ao extrair username do token atual: {}", e.getMessage());
      return null;
    }
  }
}
