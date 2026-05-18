package com.teacherdash.service;

import com.teacherdash.dto.FaturaRequest;
import com.teacherdash.dto.FaturaResponse;
import com.teacherdash.entity.Fatura;
import com.teacherdash.entity.Aluno;
import com.teacherdash.entity.Turma;
import com.teacherdash.entity.TeacherUser;
import com.teacherdash.entity.Notificacao;
import com.teacherdash.repository.FaturaRepository;
import com.teacherdash.repository.AlunoRepository;
import com.teacherdash.repository.TurmaRepository;
import com.teacherdash.repository.TeacherUserRepository;
import com.teacherdash.repository.NotificacaoRepository;
import com.teacherdash.exception.ResourceNotFoundException;
import com.teacherdash.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FaturaService {
    
    private final FaturaRepository faturaRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final TeacherUserRepository teacherUserRepository;
    private final NotificacaoRepository notificacaoRepository;
    
    // ============================================
    // CRUD BÁSICO
    // ============================================
    
    /**
     * Criar nova fatura
     */
    public FaturaResponse criar(UUID teacherId, FaturaRequest request) {
        log.info("Criando nova fatura para professor: {}", teacherId);
        
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        Aluno aluno = alunoRepository.findById(request.getAlunoId())
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado: " + request.getAlunoId()));
        
        Turma turma = turmaRepository.findById(request.getTurmaId())
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada: " + request.getTurmaId()));
        
        verificarPropriedade(teacherId, aluno.getTeacherUser().getId());
        
        Fatura fatura = Fatura.builder()
            .teacherUser(teacher)
            .aluno(aluno)
            .turma(turma)
            .valor(request.getValor())
            .descricao(request.getDescricao())
            .dataVencimento(request.getDataVencimento())
            .status(request.getStatus() != null ? request.getStatus() : "pendente")
            .metodoPagamento(request.getMetodoPagamento())
            .chavePix(request.getChavePix())
            .observacao(request.getObservacao())
            .build();
        
        Fatura saved = faturaRepository.save(fatura);
        log.info("Fatura criada: {} para professor: {}", saved.getId(), teacherId);
        
        // Notificar
        criarNotificacao(teacher, "fatura_criada", 
            "Nova fatura criada", 
            "Fatura de R$ " + request.getValor() + " para " + aluno.getNome());
        
        return mapToResponse(saved);
    }
    
    /**
     * Buscar fatura por ID
     */
    @Transactional(readOnly = true)
    public FaturaResponse buscarPorId(UUID teacherId, UUID faturaId) {
        Fatura fatura = faturaRepository.findById(faturaId)
            .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada: " + faturaId));
        
        verificarPropriedade(teacherId, fatura.getTeacherUser().getId());
        return mapToResponse(fatura);
    }
    
    /**
     * Listar faturas do professor (paginado)
     */
    @Transactional(readOnly = true)
    public Page<FaturaResponse> listarPorProfessor(UUID teacherId, Pageable pageable) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return faturaRepository.findByTeacherUser(teacher, pageable)
            .map(this::mapToResponse);
    }
    
    /**
     * Atualizar fatura
     */
    public FaturaResponse atualizar(UUID teacherId, UUID faturaId, FaturaRequest request) {
        log.info("Atualizando fatura: {} do professor: {}", faturaId, teacherId);
        
        Fatura fatura = faturaRepository.findById(faturaId)
            .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada: " + faturaId));
        
        verificarPropriedade(teacherId, fatura.getTeacherUser().getId());
        
        if (request.getValor() != null) {
            fatura.setValor(request.getValor());
        }
        if (request.getDescricao() != null) {
            fatura.setDescricao(request.getDescricao());
        }
        if (request.getDataVencimento() != null) {
            fatura.setDataVencimento(request.getDataVencimento());
        }
        if (request.getObservacao() != null) {
            fatura.setObservacao(request.getObservacao());
        }
        
        Fatura updated = faturaRepository.save(fatura);
        log.info("Fatura atualizada: {}", faturaId);
        
        return mapToResponse(updated);
    }
    
    /**
     * Deletar fatura
     */
    public void deletar(UUID teacherId, UUID faturaId) {
        log.info("Deletando fatura: {} do professor: {}", faturaId, teacherId);
        
        Fatura fatura = faturaRepository.findById(faturaId)
            .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada: " + faturaId));
        
        verificarPropriedade(teacherId, fatura.getTeacherUser().getId());
        
        faturaRepository.delete(fatura);
        log.info("Fatura deletada: {}", faturaId);
    }
    
    // ============================================
    // OPERAÇÕES DE NEGÓCIO
    // ============================================
    
    /**
     * Marcar fatura como paga
     */
    public FaturaResponse marcarComoPaga(UUID teacherId, UUID faturaId) {
        log.info("Marcando fatura como paga: {}", faturaId);
        
        Fatura fatura = faturaRepository.findById(faturaId)
            .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada: " + faturaId));
        
        verificarPropriedade(teacherId, fatura.getTeacherUser().getId());
        
        fatura.marcarComoPaga();
        Fatura updated = faturaRepository.save(fatura);
        
        // Notificar
        criarNotificacao(fatura.getTeacherUser(), "fatura_paga", 
            "Fatura paga", 
            "Fatura de R$ " + fatura.getValor() + " de " + fatura.getAluno().getNome() + " foi paga!");
        
        log.info("Fatura marcada como paga: {}", faturaId);
        return mapToResponse(updated);
    }
    
    /**
     * Listar faturas pendentes
     */
    @Transactional(readOnly = true)
    public List<FaturaResponse> listarPendentes(UUID teacherId) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return faturaRepository.findPendentesByTeacher(teacher)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Listar faturas atrasadas
     */
    @Transactional(readOnly = true)
    public List<FaturaResponse> listarAtrasadas(UUID teacherId) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return faturaRepository.findAtrasadasByTeacher(teacher)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Próximas faturas a vencer
     */
    @Transactional(readOnly = true)
    public List<FaturaResponse> proximasAVencer(UUID teacherId) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return faturaRepository.findProximasAVencer(teacher)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Total recebido (pago)
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalRecebido(UUID teacherId) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return faturaRepository.getTotalRecebido(teacher);
    }
    
    /**
     * Total pendente
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalPendente(UUID teacherId) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return faturaRepository.getTotalPendente(teacher);
    }
    
    /**
     * Receita do mês atual
     */
    @Transactional(readOnly = true)
    public BigDecimal getReceitaMes(UUID teacherId) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return faturaRepository.getReceitaMes(teacher);
    }
    
    /**
     * Contar faturas pendentes
     */
    @Transactional(readOnly = true)
    public long countPendentes(UUID teacherId) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return faturaRepository.countPendentesByTeacher(teacher);
    }
    
    // ============================================
    // UTILITÁRIOS
    // ============================================
    
    private void verificarPropriedade(UUID teacherId, UUID faturaTeacherId) {
        if (!teacherId.equals(faturaTeacherId)) {
            throw new UnauthorizedException("Você não tem permissão para acessar esta fatura");
        }
    }
    
    private void criarNotificacao(TeacherUser teacher, String tipo, String titulo, String mensagem) {
        Notificacao notif = Notificacao.builder()
            .teacherUser(teacher)
            .tipo(tipo)
            .titulo(titulo)
            .mensagem(mensagem)
            .lida(false)
            .build();
        notificacaoRepository.save(notif);
    }
    
    private FaturaResponse mapToResponse(Fatura fatura) {
        return FaturaResponse.builder()
            .id(fatura.getId())
            .alunoId(fatura.getAluno().getId())
            .alunoNome(fatura.getAluno().getNome())
            .turmaId(fatura.getTurma().getId())
            .turmaNome(fatura.getTurma().getNome())
            .valor(fatura.getValor())
            .descricao(fatura.getDescricao())
            .dataVencimento(fatura.getDataVencimento())
            .dataPagamento(fatura.getDataPagamento())
            .status(fatura.getStatus())
            .metodoPagamento(fatura.getMetodoPagamento())
            .observacao(fatura.getObservacao())
            .atrasada(fatura.isAtrasada())
            .criadoEm(fatura.getCriadoEm())
            .atualizadoEm(fatura.getAtualizadoEm())
            .build();
    }
}
