package com.teacherdash.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    
    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;
    
    @Value("${spring.security.jwt.expiration}")
    private long jwtExpiration;
    
    // ============================================
    // GERAÇÃO DE TOKEN
    // ============================================
    
    /**
     * Gerar token JWT para um professor
     */
    public String generateToken(UUID teacherId, String email) {
        return generateTokenWithClaims(teacherId, email, new HashMap<>());
    }
    
    /**
     * Gerar token JWT com claims customizados
     */
    public String generateTokenWithClaims(UUID teacherId, String email, Map<String, Object> claims) {
        Map<String, Object> allClaims = new HashMap<>(claims);
        allClaims.put("email", email);
        allClaims.put("role", "USER");
        
        return createToken(allClaims, teacherId.toString());
    }
    
    /**
     * Criar token JWT
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.builder()
            .subject(subject)
            .claims(claims)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }
    
    // ============================================
    // VALIDAÇÃO DE TOKEN
    // ============================================
    
    /**
     * Validar token JWT
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            
            return true;
        } catch (SecurityException ex) {
            log.error("Assinatura JWT inválida: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Token JWT inválido: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Token JWT expirado: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Token JWT não suportado: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Claims JWT vazios: {}", ex.getMessage());
        }
        
        return false;
    }
    
    // ============================================
    // EXTRAÇÃO DE DADOS
    // ============================================
    
    /**
     * Extrair teacherId do token
     */
    public UUID getTeacherIdFromToken(String token) {
        String subject = getAllClaimsFromToken(token).getSubject();
        return UUID.fromString(subject);
    }
    
    /**
     * Extrair email do token
     */
    public String getEmailFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (String) claims.get("email");
    }
    
    /**
     * Extrair role do token
     */
    public String getRoleFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (String) claims.get("role");
    }
    
    /**
     * Verificar se token está expirado
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }
    
    /**
     * Extrair todos os claims do token
     */
    private Claims getAllClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
    
    // ============================================
    // UTILITÁRIOS
    // ============================================
    
    /**
     * Extrair token do header Authorization
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    
    /**
     * Tempo restante do token em segundos
     */
    public long getExpirationTime(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;
    }
}
