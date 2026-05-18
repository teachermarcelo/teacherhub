package com.teacherdash.config;

import com.teacherdash.security.JwtAuthenticationFilter;
import com.teacherdash.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    // ============================================
    // SECURITY FILTER CHAIN
    // ============================================
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desabilitar CSRF (usamos JWT)
            .csrf(csrf -> csrf.disable())
            
            // CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Session management
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Autorização
            .authorizeHttpRequests(auth -> auth
                // Públicas (sem autenticação)
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/professores").permitAll() // Registro
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                
                // Autenticadas
                .requestMatchers(HttpMethod.GET, "/api/v1/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/**").authenticated()
                
                // Tudo o mais rejeita
                .anyRequest().authenticated()
            )
            
            // JWT Filter
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
            );
        
        return http.build();
    }
    
    // ============================================
    // PASSWORD ENCODER
    // ============================================
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    // ============================================
    // AUTHENTICATION MANAGER
    // ============================================
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    // ============================================
    // CORS CONFIGURATION
    // ============================================
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origins permitidas
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:8080",
            "https://teacherdash.com",
            "https://www.teacherdash.com"
        ));
        
        // Métodos permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
            "Content-Type",
            "Authorization",
            "Accept",
            "Origin",
            "X-Requested-With"
        ));
        
        // Headers expostos
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type"
        ));
        
        // Credenciais
        configuration.setAllowCredentials(true);
        
        // Max age
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
