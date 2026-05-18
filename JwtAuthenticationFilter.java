package com.teacherdash.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Extrair token do header
            String token = getJwtFromRequest(request);
            
            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                
                // Extrair dados do token
                UUID teacherId = tokenProvider.getTeacherIdFromToken(token);
                String email = tokenProvider.getEmailFromToken(token);
                String role = tokenProvider.getRoleFromToken(token);
                
                // Configurar SecurityContext (sem usar Authentication customizado)
                // Apenas adicionar como atributo para usar em controllers
                request.setAttribute("teacherId", teacherId);
                request.setAttribute("email", email);
                request.setAttribute("role", role);
                
                log.debug("JWT validado para professor: {}", teacherId);
            }
        } catch (Exception ex) {
            log.error("Erro ao processar JWT", ex);
        }
        
        // Continuar com a cadeia de filtros
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extrair JWT do header Authorization
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
