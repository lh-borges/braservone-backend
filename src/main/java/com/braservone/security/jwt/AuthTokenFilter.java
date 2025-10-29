package com.braservone.security.jwt;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.braservone.security.services.UserDetailsServiceImpl;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String path = request.getRequestURI();
        final String method = request.getMethod();

        // 1) Ignora preflight CORS
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Ignora rotas públicas (ajuste conforme seu projeto)
        if (shouldSkip(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = extractBearerToken(request)
                    .or(() -> extractTokenFromCookie(request, "accessToken"))
                    .orElse(null);

            log.debug("[JWT] path={} method={} hasToken={}", path, method, jwt != null);

            // 3) Evita reautenticar se já houver auth no contexto
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            if (currentAuth != null && currentAuth.isAuthenticated()) {
                filterChain.doFilter(request, response);
                return;
            }

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                log.debug("[JWT] token válido; usuário extraído={}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("[SECURITY] Authentication definido para usuário={}", username);
            } else {
                log.debug("[JWT] Ausente ou inválido");
            }

        } catch (ExpiredJwtException e) {
            log.warn("[JWT] Token expirado: {}", safeToken(e.getClaims() != null ? e.getClaims().getSubject() : null));
        } catch (SignatureException e) {
            log.warn("[JWT] Assinatura inválida");
        } catch (MalformedJwtException e) {
            log.warn("[JWT] Token malformado");
        } catch (IllegalArgumentException e) {
            log.warn("[JWT] Token vazio/ilegal");
        } catch (Exception e) {
            // Não propague erro aqui; deixe o EntryPoint tratar o 401.
            log.error("[JWT] Erro inesperado no filtro: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    /** Defina os prefixos/paths públicos do seu backend. */
    private boolean shouldSkip(String path) {
        // Ajuste conforme sua convenção
        return path.startsWith("/api/auth/")
            || path.startsWith("/swagger")
            || path.startsWith("/v3/api-docs")
            || path.startsWith("/actuator/health");
    }

    /** Extrai Bearer do header Authorization (case-insensitive, com trim seguro). */
    private Optional<String> extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null) return Optional.empty();

        String h = header.trim();
        if (h.length() < 7) return Optional.empty();

        String prefix = h.substring(0, 6).toLowerCase(Locale.ROOT);
        if (!"bearer".equals(prefix)) return Optional.empty();

        String token = h.substring(6).trim(); // remove "Bearer" e espaços
        return token.isEmpty() ? Optional.empty() : Optional.of(token);
    }

    /** Fallback: tenta pegar de cookie HttpOnly (ex.: "accessToken"). */
    private Optional<String> extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return Optional.empty();
        for (Cookie c : request.getCookies()) {
            if (cookieName.equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                return Optional.of(c.getValue());
            }
        }
        return Optional.empty();
    }

    /** Evita logar token inteiro; mostra apenas sujeito/usuário quando possível. */
    private String safeToken(String subject) {
        return subject == null ? "<unknown-subject>" : subject;
    }
}
