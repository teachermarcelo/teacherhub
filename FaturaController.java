package com.teacherdash.controller;

import com.teacherdash.dto.ApiResponse;
import com.teacherdash.dto.PageResponse;
import com.teacherdash.dto.FaturaRequest;
import com.teacherdash.dto.FaturaResponse;
import com.teacherdash.service.FaturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/faturas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class FaturaController {
    
    private final FaturaService faturaService;
    
    // ============================================
    // CRUD BÁSICO
    // ============================================
    
    /**
     * POST /api/v1/faturas
     * Criar nova fatura
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<FaturaResponse>> criar(
            @RequestAttribute("teacherId") UUID teacherId,
            @Valid @RequestBody FaturaRequest request) {
        
        log.info("POST /faturas - Criando fatura para professor: {}", teacherId);
        
        FaturaResponse response = faturaService.criar(teacherId, request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Fatura criada com sucesso", response));
    }
    
    /**
     * GET /api/v1/faturas/{id}
     * Buscar fatura por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<FaturaResponse>> buscarPorId(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id) {
        
        log.info("GET /faturas/{} - Buscando fatura", id);
        
        FaturaResponse response = faturaService.buscarPorId(teacherId, id);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Fatura encontrada", response));
    }
    
    /**
     * GET /api/v1/faturas
     * Listar faturas do professor (paginado)
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PageResponse<FaturaResponse>>> listar(
            @RequestAttribute("teacherId") UUID teacherId,
            Pageable pageable) {
        
        log.info("GET /faturas - Listando faturas do professor: {}", teacherId);
        
        Page<FaturaResponse> page = faturaService.listarPorProfessor(teacherId, pageable);
        PageResponse<FaturaResponse> response = PageResponse.from(page);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Faturas listadas", response));
    }
    
    /**
     * PUT /api/v1/faturas/{id}
     * Atualizar fatura
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<FaturaResponse>> atualizar(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id,
            @Valid @RequestBody FaturaRequest request) {
        
        log.info("PUT /faturas/{} - Atualizando fatura", id);
        
        FaturaResponse response = faturaService.atualizar(teacherId, id, request);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Fatura atualizada com sucesso", response));
    }
    
    /**
     * DELETE /api/v1/faturas/{id}
     * Deletar fatura
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deletar(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id) {
        
        log.info("DELETE /faturas/{} - Deletando fatura", id);
        
        faturaService.deletar(teacherId, id);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Fatura deletada com sucesso", null));
    }
    
    // ============================================
    // OPERAÇÕES DE NEGÓCIO
    // ============================================
    
    /**
     * PUT /api/v1/faturas/{id}/pagar
     * Marcar fatura como paga
     */
    @PutMapping("/{id}/pagar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<FaturaResponse>> marcarComoPaga(
            @RequestAttribute("teacherId") UUID teacherId,
            @PathVariable UUID id) {
        
        log.info("PUT /faturas/{}/pagar - Marcando como paga", id);
        
        FaturaResponse response = faturaService.marcarComoPaga(teacherId, id);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Fatura marcada como paga", response));
    }
    
    /**
     * GET /api/v1/faturas/pendentes
     * Listar faturas pendentes
     */
    @GetMapping("/filtro/pendentes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<FaturaResponse>>> listarPendentes(
            @RequestAttribute("teacherId") UUID teacherId) {
        
        log.info("GET /faturas/filtro/pendentes - Listando pendentes");
        
        List<FaturaResponse> response = faturaService.listarPendentes(teacherId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Faturas pendentes listadas", response));
    }
    
    /**
     * GET /api/v1/faturas/atrasadas
     * Listar faturas atrasadas
     */
    @GetMapping("/filtro/atrasadas")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<FaturaResponse>>> listarAtrasadas(
            @RequestAttribute("teacherId") UUID teacherId) {
        
        log.info("GET /faturas/filtro/atrasadas - Listando atrasadas");
        
        List<FaturaResponse> response = faturaService.listarAtrasadas(teacherId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Faturas atrasadas listadas", response));
    }
    
    /**
     * GET /api/v1/faturas/proximas-vencer
     * Próximas faturas a vencer
     */
    @GetMapping("/filtro/proximas-vencer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<FaturaResponse>>> proximasAVencer(
            @RequestAttribute("teacherId") UUID teacherId) {
        
        log.info("GET /faturas/filtro/proximas-vencer - Listando próximas");
        
        List<FaturaResponse> response = faturaService.proximasAVencer(teacherId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Próximas faturas a vencer", response));
    }
    
    // ============================================
    // RELATÓRIOS FINANCEIROS
    // ============================================
    
    /**
     * GET /api/v1/faturas/relatorio/total-recebido
     * Total recebido (pago)
     */
    @GetMapping("/relatorio/total-recebido")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalRecebido(
            @RequestAttribute("teacherId") UUID teacherId) {
        
        log.info("GET /faturas/relatorio/total-recebido");
        
        BigDecimal response = faturaService.getTotalRecebido(teacherId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Total recebido", response));
    }
    
    /**
     * GET /api/v1/faturas/relatorio/total-pendente
     * Total pendente
     */
    @GetMapping("/relatorio/total-pendente")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalPendente(
            @RequestAttribute("teacherId") UUID teacherId) {
        
        log.info("GET /faturas/relatorio/total-pendente");
        
        BigDecimal response = faturaService.getTotalPendente(teacherId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Total pendente", response));
    }
    
    /**
     * GET /api/v1/faturas/relatorio/receita-mes
     * Receita do mês atual
     */
    @GetMapping("/relatorio/receita-mes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<BigDecimal>> getReceitaMes(
            @RequestAttribute("teacherId") UUID teacherId) {
        
        log.info("GET /faturas/relatorio/receita-mes");
        
        BigDecimal response = faturaService.getReceitaMes(teacherId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Receita do mês", response));
    }
    
    /**
     * GET /api/v1/faturas/relatorio/count-pendentes
     * Contar faturas pendentes
     */
    @GetMapping("/relatorio/count-pendentes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Long>> countPendentes(
            @RequestAttribute("teacherId") UUID teacherId) {
        
        log.info("GET /faturas/relatorio/count-pendentes");
        
        long response = faturaService.countPendentes(teacherId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Total de faturas pendentes", response));
    }
}
