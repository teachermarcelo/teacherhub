package com.teacherdash.controller;

import com.teacherdash.dto.ApiResponse;
import com.teacherdash.dto.TeacherUserRequest;
import com.teacherdash.dto.TeacherUserResponse;
import com.teacherdash.service.TeacherUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/professores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class TeacherUserController {
    
    private final TeacherUserService teacherUserService;
    
    // ============================================
    // CRUD BÁSICO
    // ============================================
    
    /**
     * POST /api/v1/professores
     * Criar novo professor (register)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TeacherUserResponse>> criar(
            @Valid @RequestBody TeacherUserRequest request) {
        
        log.info("POST /professores - Criando novo professor: {}", request.getEmail());
        
        TeacherUserResponse response = teacherUserService.criar(request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Professor criado com sucesso", response));
    }
    
    /**
     * GET /api/v1/professores/{id}
     * Buscar professor por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TeacherUserResponse>> buscarPorId(
            @PathVariable UUID id) {
        
        log.info("GET /professores/{} - Buscando professor", id);
        
        TeacherUserResponse response = teacherUserService.buscarPorId(id);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Professor encontrado", response));
    }
    
    /**
     * GET /api/v1/professores/email/{email}
     * Buscar professor por email
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TeacherUserResponse>> buscarPorEmail(
            @PathVariable String email) {
        
        log.info("GET /professores/email/{} - Buscando por email", email);
        
        TeacherUserResponse response = teacherUserService.buscarPorEmail(email);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Professor encontrado", response));
    }
    
    /**
     * GET /api/v1/professores/me
     * Buscar dados do professor logado
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TeacherUserResponse>> getMeuPerfil(
            @RequestAttribute("teacherId") UUID teacherId) {
        
        log.info("GET /professores/me - Buscando meu perfil");
        
        TeacherUserResponse response = teacherUserService.buscarPorId(teacherId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Perfil encontrado", response));
    }
    
    /**
     * PUT /api/v1/professores/{id}
     * Atualizar professor
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TeacherUserResponse>> atualizar(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id,
            @Valid @RequestBody TeacherUserRequest request) {
        
        log.info("PUT /professores/{} - Atualizando professor", id);
        
        // Validar se está atualizando seu próprio perfil
        if (!teacherId.equals(id)) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(false, "Você só pode atualizar seu próprio perfil", null));
        }
        
        TeacherUserResponse response = teacherUserService.atualizar(id, request);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Professor atualizado com sucesso", response));
    }
    
    /**
     * DELETE /api/v1/professores/{id}
     * Deletar professor
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deletar(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id) {
        
        log.info("DELETE /professores/{} - Deletando professor", id);
        
        // Validar se está deletando sua própria conta
        if (!teacherId.equals(id)) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(false, "Você só pode deletar sua própria conta", null));
        }
        
        teacherUserService.deletar(id);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Professor deletado com sucesso", null));
    }
    
    // ============================================
    // OPERAÇÕES DE NEGÓCIO
    // ============================================
    
    /**
     * PUT /api/v1/professores/{id}/pix/ativar
     * Ativar PIX
     */
    @PutMapping("/{id}/pix/ativar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TeacherUserResponse>> ativarPix(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id,
            @RequestParam String chavePix) {
        
        log.info("PUT /professores/{}/pix/ativar - Ativando PIX", id);
        
        if (!teacherId.equals(id)) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(false, "Não autorizado", null));
        }
        
        TeacherUserResponse response = teacherUserService.ativarPix(id, chavePix);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "PIX ativado com sucesso", response));
    }
    
    /**
     * PUT /api/v1/professores/{id}/pix/desativar
     * Desativar PIX
     */
    @PutMapping("/{id}/pix/desativar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TeacherUserResponse>> desativarPix(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id) {
        
        log.info("PUT /professores/{}/pix/desativar - Desativando PIX", id);
        
        if (!teacherId.equals(id)) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(false, "Não autorizado", null));
        }
        
        TeacherUserResponse response = teacherUserService.desativarPix(id);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "PIX desativado com sucesso", response));
    }
    
    /**
     * PUT /api/v1/professores/{id}/plano
     * Upgrade de plano
     */
    @PutMapping("/{id}/plano")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TeacherUserResponse>> upgradePlano(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id,
            @RequestParam String novoPlano) {
        
        log.info("PUT /professores/{}/plano - Upgrade para: {}", id, novoPlano);
        
        if (!teacherId.equals(id)) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(false, "Não autorizado", null));
        }
        
        TeacherUserResponse response = teacherUserService.upgradePlano(id, novoPlano);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Plano atualizado com sucesso", response));
    }
}
