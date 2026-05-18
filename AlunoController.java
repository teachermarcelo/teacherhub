package com.teacherdash.controller;

import com.teacherdash.dto.ApiResponse;
import com.teacherdash.dto.PageResponse;
import com.teacherdash.dto.AlunoRequest;
import com.teacherdash.dto.AlunoResponse;
import com.teacherdash.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/alunos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AlunoController {
    
    private final AlunoService alunoService;
    
    // ============================================
    // CRUD BÁSICO
    // ============================================
    
    /**
     * POST /api/v1/alunos
     * Criar novo aluno
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<AlunoResponse>> criar(
            @RequestAttribute("teacherId") UUID teacherId,
            @Valid @RequestBody AlunoRequest request) {
        
        log.info("POST /alunos - Criando novo aluno para professor: {}", teacherId);
        
        AlunoResponse response = alunoService.criar(teacherId, request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Aluno criado com sucesso", response));
    }
    
    /**
     * GET /api/v1/alunos/{id}
     * Buscar aluno por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<AlunoResponse>> buscarPorId(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id) {
        
        log.info("GET /alunos/{} - Buscando aluno", id);
        
        AlunoResponse response = alunoService.buscarPorId(teacherId, id);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Aluno encontrado", response));
    }
    
    /**
     * GET /api/v1/alunos
     * Listar alunos do professor (paginado)
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PageResponse<AlunoResponse>>> listar(
            @RequestAttribute("teacherId") UUID teacherId,
            Pageable pageable) {
        
        log.info("GET /alunos - Listando alunos do professor: {}", teacherId);
        
        Page<AlunoResponse> page = alunoService.listarPorProfessor(teacherId, pageable);
        PageResponse<AlunoResponse> response = PageResponse.from(page);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Alunos listados", response));
    }
    
    /**
     * PUT /api/v1/alunos/{id}
     * Atualizar aluno
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<AlunoResponse>> atualizar(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id,
            @Valid @RequestBody AlunoRequest request) {
        
        log.info("PUT /alunos/{} - Atualizando aluno", id);
        
        AlunoResponse response = alunoService.atualizar(teacherId, id, request);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Aluno atualizado com sucesso", response));
    }
    
    /**
     * DELETE /api/v1/alunos/{id}
     * Deletar aluno
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deletar(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id) {
        
        log.info("DELETE /alunos/{} - Deletando aluno", id);
        
        alunoService.deletar(teacherId, id);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Aluno deletado com sucesso", null));
    }
    
    // ============================================
    // OPERAÇÕES DE NEGÓCIO
    // ============================================
    
    /**
     * GET /api/v1/alunos/ativos
     * Listar alunos ativos
     */
    @GetMapping("/filtro/ativos")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<AlunoResponse>>> listarAtivos(
            @RequestAttribute("teacherId") UUID teacherId) {
        
        log.info("GET /alunos/filtro/ativos - Listando ativos");
        
        List<AlunoResponse> response = alunoService.listarAtivos(teacherId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Alunos ativos listados", response));
    }
    
    /**
     * GET /api/v1/alunos/buscar
     * Buscar alunos por nome
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<AlunoResponse>>> buscarPorNome(
            @RequestAttribute("teacherId") UUID teacherId,
            @RequestParam String nome) {
        
        log.info("GET /alunos/buscar - Buscando por nome: {}", nome);
        
        List<AlunoResponse> response = alunoService.buscarPorNome(teacherId, nome);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Alunos encontrados", response));
    }
    
    /**
     * PUT /api/v1/alunos/{id}/inativar
     * Marcar aluno como inativo
     */
    @PutMapping("/{id}/inativar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<AlunoResponse>> marcarInativo(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id) {
        
        log.info("PUT /alunos/{}/inativar - Marcando como inativo", id);
        
        AlunoResponse response = alunoService.marcarInativo(teacherId, id);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Aluno marcado como inativo", response));
    }
    
    /**
     * PUT /api/v1/alunos/{id}/reativar
     * Reativar aluno
     */
    @PutMapping("/{id}/reativar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<AlunoResponse>> reativar(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id) {
        
        log.info("PUT /alunos/{}/reativar - Reativando aluno", id);
        
        AlunoResponse response = alunoService.reativar(teacherId, id);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Aluno reativado com sucesso", response));
    }
    
    /**
     * GET /api/v1/alunos/relatorio/contar-ativos
     * Contar alunos ativos
     */
    @GetMapping("/relatorio/contar-ativos")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Long>> contarAtivos(
            @RequestAttribute("teacherId") UUID teacherId) {
        
        log.info("GET /alunos/relatorio/contar-ativos");
        
        long response = alunoService.contarAtivos(teacherId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Total de alunos ativos", response));
    }
}
