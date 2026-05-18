package com.teacherdash.controller;

import com.teacherdash.dto.ApiResponse;
import com.teacherdash.dto.PageResponse;
import com.teacherdash.dto.TurmaRequest;
import com.teacherdash.dto.TurmaResponse;
import com.teacherdash.service.TurmaService;
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
@RequestMapping("/api/v1/turmas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class TurmaController {
    
    private final TurmaService turmaService;
    
    // ============================================
    // CRUD BÁSICO
    // ============================================
    
    /**
     * POST /api/v1/turmas
     * Criar nova turma
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TurmaResponse>> criar(
            @RequestAttribute("teacherId") UUID teacherId,
            @Valid @RequestBody TurmaRequest request) {
        
        log.info("POST /turmas - Criando nova turma para professor: {}", teacherId);
        
        TurmaResponse response = turmaService.criar(teacherId, request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Turma criada com sucesso", response));
    }
    
    /**
     * GET /api/v1/turmas/{id}
     * Buscar turma por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TurmaResponse>> buscarPorId(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id) {
        
        log.info("GET /turmas/{} - Buscando turma", id);
        
        TurmaResponse response = turmaService.buscarPorId(teacherId, id);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Turma encontrada", response));
    }
    
    /**
     * GET /api/v1/turmas
     * Listar turmas do professor (paginado)
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PageResponse<TurmaResponse>>> listar(
            @RequestAttribute("teacherId") UUID teacherId,
            Pageable pageable) {
        
        log.info("GET /turmas - Listando turmas do professor: {}", teacherId);
        
        Page<TurmaResponse> page = turmaService.listarPorProfessor(teacherId, pageable);
        PageResponse<TurmaResponse> response = PageResponse.from(page);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Turmas listadas", response));
    }
    
    /**
     * PUT /api/v1/turmas/{id}
     * Atualizar turma
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TurmaResponse>> atualizar(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id,
            @Valid @RequestBody TurmaRequest request) {
        
        log.info("PUT /turmas/{} - Atualizando turma", id);
        
        TurmaResponse response = turmaService.atualizar(teacherId, id, request);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Turma atualizada com sucesso", response));
    }
    
    /**
     * DELETE /api/v1/turmas/{id}
     * Deletar turma
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deletar(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id) {
        
        log.info("DELETE /turmas/{} - Deletando turma", id);
        
        turmaService.deletar(teacherId, id);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Turma deletada com sucesso", null));
    }
    
    // ============================================
    // OPERAÇÕES DE NEGÓCIO
    // ============================================
    
    /**
     * GET /api/v1/turmas/ativas
     * Listar turmas ativas
     */
    @GetMapping("/filtro/ativas")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<TurmaResponse>>> listarAtivas(
            @RequestAttribute("teacherId") UUID teacherId) {
        
        log.info("GET /turmas/filtro/ativas - Listando ativas");
        
        List<TurmaResponse> response = turmaService.listarAtivas(teacherId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Turmas ativas listadas", response));
    }
    
    /**
     * GET /api/v1/turmas/buscar
     * Buscar turmas por nome
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<TurmaResponse>>> buscarPorNome(
            @RequestAttribute("teacherId") UUID teacherId,
            @RequestParam String nome) {
        
        log.info("GET /turmas/buscar - Buscando por nome: {}", nome);
        
        List<TurmaResponse> response = turmaService.buscarPorNome(teacherId, nome);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Turmas encontradas", response));
    }
    
    /**
     * GET /api/v1/turmas/aulas-hoje
     * Turmas com aula hoje
     */
    @GetMapping("/filtro/aulas-hoje")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<TurmaResponse>>> turmasComAulaHoje(
            @RequestAttribute("teacherId") UUID teacherId) {
        
        log.info("GET /turmas/filtro/aulas-hoje - Listando aulas de hoje");
        
        List<TurmaResponse> response = turmaService.turmasComAulaHoje(teacherId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Aulas de hoje listadas", response));
    }
    
    /**
     * GET /api/v1/turmas/relatorio/contar-ativas
     * Contar turmas ativas
     */
    @GetMapping("/relatorio/contar-ativas")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Long>> contarAtivas(
            @RequestAttribute("teacherId") UUID teacherId) {
        
        log.info("GET /turmas/relatorio/contar-ativas");
        
        long response = turmaService.contarAtivas(teacherId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Total de turmas ativas", response));
    }
}
