package com.teacherdash.controller;

import com.teacherdash.dto.ApiResponse;
import com.teacherdash.dto.LoginRequest;
import com.teacherdash.dto.LoginResponse;
import com.teacherdash.dto.RegisterRequest;
import com.teacherdash.dto.TeacherUserResponse;
import com.teacherdash.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private final AuthService authService;
    
    // ============================================
    // AUTENTICAÇÃO
    // ============================================
    
    /**
     * POST /api/v1/auth/login
     * Login com email e senha
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        
        log.info("POST /auth/login - Login para: {}", request.getEmail());
        
        LoginResponse response = authService.login(request.getEmail(), request.getSenha());
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Login realizado com sucesso", response));
    }
    
    /**
     * POST /api/v1/auth/register
     * Registrar novo professor
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        
        log.info("POST /auth/register - Registrando novo professor: {}", request.getEmail());
        
        LoginResponse response = authService.register(request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Professor registrado com sucesso", response));
    }
    
    /**
     * POST /api/v1/auth/refresh
     * Renovar token JWT
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @RequestAttribute("teacherId") UUID teacherId,
            @RequestAttribute("email") String email) {
        
        log.info("POST /auth/refresh - Renovando token para: {}", email);
        
        LoginResponse response = authService.refreshToken(teacherId, email);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Token renovado com sucesso", response));
    }
    
    /**
     * POST /api/v1/auth/logout
     * Logout (operação no cliente, apenas confirma)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestAttribute("teacherId") UUID teacherId) {
        
        log.info("POST /auth/logout - Logout para professor: {}", teacherId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Logout realizado com sucesso", null));
    }
    
    /**
     * GET /api/v1/auth/verify
     * Verificar se token é válido
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<TeacherUserResponse>> verifyToken(
            @RequestAttribute("teacherId") UUID teacherId) {
        
        log.info("GET /auth/verify - Verificando token para professor: {}", teacherId);
        
        TeacherUserResponse response = authService.buscarProfessor(teacherId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Token válido", response));
    }
    
    // ============================================
    // RESET DE SENHA (simplificado)
    // ============================================
    
    /**
     * POST /api/v1/auth/forgot-password
     * Solicitar reset de senha (envia link por email)
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @RequestParam String email) {
        
        log.info("POST /auth/forgot-password - Solicitação para: {}", email);
        
        authService.forgotPassword(email);
        
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            "Instruções de reset enviadas para o email (se cadastrado)",
            null
        ));
    }
    
    /**
     * POST /api/v1/auth/reset-password
     * Resetar senha com token
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String token,
            @RequestParam String novaSenha) {
        
        log.info("POST /auth/reset-password - Reset de senha");
        
        authService.resetPassword(token, novaSenha);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Senha alterada com sucesso", null));
    }
    
    /**
     * POST /api/v1/auth/change-password
     * Mudar senha do professor logado
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestAttribute("teacherId") UUID teacherId,
            @RequestParam String senhaAtual,
            @RequestParam String novaSenha) {
        
        log.info("POST /auth/change-password - Mudando senha para professor: {}", teacherId);
        
        authService.changePassword(teacherId, senhaAtual, novaSenha);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Senha alterada com sucesso", null));
    }
}
