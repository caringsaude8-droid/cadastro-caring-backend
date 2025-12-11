package com.caring.cadastro.operadora.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        logger.info("[JwtAuthenticationFilter] shouldNotFilter? path: {} method: {}", path, request.getMethod());
        return path.equals("/api/cadastro/v1/usuarios/login") ||
                path.equals("/api/cadastro/v1/usuarios/refresh") ||
                request.getMethod().equals("OPTIONS");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        logger.info("[JwtAuthenticationFilter] doFilterInternal - path: {} method: {}", request.getRequestURI(), request.getMethod());
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            logger.info("[JwtAuthenticationFilter] Recebido token JWT: {}", token);
            try {
                if (jwtUtil.validateToken(token)) {
                    String subject = jwtUtil.getSubject(token);
                    logger.info("[JwtAuthenticationFilter] Token válido. Subject: {}", subject);
                    if (subject != null) {
                        var claims = jwtUtil.getClaimsFromToken(token);
                        String perfil = claims != null && claims.get("perfil") != null ?
                            claims.get("perfil").toString() : "USER";
                        logger.info("[JwtAuthenticationFilter] Perfil extraído do token: {}", perfil);
                        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + perfil));
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            subject, null, authorities);
                        auth.setDetails(request);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } else {
                    logger.warn("[JwtAuthenticationFilter] Token JWT inválido ou expirado.");
                }
            } catch (Exception ex) {
                logger.error("[JwtAuthenticationFilter] Falha na validação do token JWT: {}", ex.getMessage(), ex);
            }
        } else {
            logger.warn("[JwtAuthenticationFilter] Header Authorization ausente ou mal formatado.");
        }
        filterChain.doFilter(request, response);
    }

    private void respondUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String body = String.format("{\"status\":%d,\"message\":\"%s\"}", HttpServletResponse.SC_UNAUTHORIZED, "Token inválido ou expirado");
        response.getWriter().write(body);
    }
}
