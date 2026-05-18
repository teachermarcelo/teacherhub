package com.teacherdash.service;

import com.teacherdash.dto.TurmaRequest;
import com.teacherdash.dto.TurmaResponse;
import com.teacherdash.entity.Turma;
import com.teacherdash.entity.TeacherUser;
import com.teacherdash.repository.TurmaRepository;
import com.teacherdash.repository.TeacherUserRepository;
import com.teacherdash.repository.InscricaoRepository;
import com.teacherdash.exception.ResourceNotFoundException;
import com.teacherdash.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TurmaService {
    
    private final TurmaRepository turmaRepository;
    private final TeacherUserRepository teacherUserRepository;
    private final InscricaoRepository inscricaoRepository;
    
    // ============================================
    // CRUD BÁSICO
    // ============================================
    
    /**
     * Criar nova turma
     */
    public TurmaResponse criar(UUID teacherId, TurmaRequest request) {
        log.info("Criando nova turma para professor: {}", teacherId);
        
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        Turma turma = Turma.builder()
            .teacherUser(teacher)
            .nome(request.getNome())
            .descricao(request.getDescricao())
            .horario(request.getHorario())
            .proximaAula(request.getProximaAula())
            .status(request.getStatus() != null ? request.getStatus() : "ativa")
            .totalAlunos(0)
            .mediaTurma(BigDecimal.ZERO)
            .build();
        
        Turma saved = turmaRepository.save(turma);
        
        // Atualizar número de turmas do professor
        teacher.setNumTurmas(teacher.getNumTurmas() + 1);
        teacherUserRepository.save(teacher);
        
        log.info("Turma criada: {} para professor: {}", saved.getId(), teacherId);
        return mapToResponse(saved);
    }
    
    /**
     * Buscar turma por ID
     */
    @Transactional(readOnly = true)
    public TurmaResponse buscarPorId(UUID teacherId, UUID turmaId) {
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada: " + turmaId));
        
        verificarPropriedade(teacherId, turma.getTeacherUser().getId());
        return mapToResponse(turma);
    }
    
    /**
     * Listar turmas do professor (paginado)
     */
    @Transactional(readOnly = true)
    public Page<TurmaResponse> listarPorProfessor(UUID teacherId, Pageable pageable) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return turmaRepository.findByTeacherUser(teacher, pageable)
            .map(this::mapToResponse);
    }
    
    /**
     * Listar turmas ativas
     */
    @Transactional(readOnly = true)
    public List<TurmaResponse> listarAtivas(UUID teacherId) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return turmaRepository.findAtivasByTeacher(teacher)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Atualizar turma
     */
    public TurmaResponse atualizar(UUID teacherId, UUID turmaId, TurmaRequest request) {
        log.info("Atualizando turma: {} do professor: {}", turmaId, teacherId);
        
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada: " + turmaId));
        
        verificarPropriedade(teacherId, turma.getTeacherUser().getId());
        
        if (request.getNome() != null) {
            turma.setNome(request.getNome());
        }
        if (request.getDescricao() != null) {
            turma.setDescricao(request.getDescricao());
        }
        if (request.getHorario() != null) {
            turma.setHorario(request.getHorario());
        }
        if (request.getProximaAula() != null) {
            turma.setProximaAula(request.getProximaAula());
        }
        if (request.getStatus() != null) {
            turma.setStatus(request.getStatus());
        }
        
        Turma updated = turmaRepository.save(turma);
        log.info("Turma atualizada: {}", turmaId);
        
        return mapToResponse(updated);
    }
    
    /**
     * Deletar turma
     */
    public void deletar(UUID teacherId, UUID turmaId) {
        log.info("Deletando turma: {} do professor: {}", turmaId, teacherId);
        
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada: " + turmaId));
        
        verificarPropriedade(teacherId, turma.getTeacherUser().getId());
        
        // Atualizar número de turmas do professor
        TeacherUser teacher = turma.getTeacherUser();
        teacher.setNumTurmas(Math.max(0, teacher.getNumTurmas() - 1));
        teacherUserRepository.save(teacher);
        
        turmaRepository.delete(turma);
        log.info("Turma deletada: {}", turmaId);
    }
    
    // ============================================
    // OPERAÇÕES DE NEGÓCIO
    // ============================================
    
    /**
     * Buscar turmas por nome
     */
    @Transactional(readOnly = true)
    public List<TurmaResponse> buscarPorNome(UUID teacherId, String nome) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return turmaRepository.searchByNome(teacher, nome)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Atualizar média da turma (baseado em inscrições)
     */
    public void atualizarMediaTurma(UUID turmaId) {
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada: " + turmaId));
        
        Double media = inscricaoRepository.getMediaTurma(turma);
        turma.setMediaTurma(media != null ? BigDecimal.valueOf(media) : BigDecimal.ZERO);
        
        turmaRepository.save(turma);
    }
    
    /**
     * Atualizar total de alunos da turma
     */
    public void atualizarTotalAlunos(UUID turmaId) {
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada: " + turmaId));
        
        long total = inscricaoRepository.findByTurmaId(turmaId).size();
        turma.setTotalAlunos((int) total);
        
        turmaRepository.save(turma);
    }
    
    /**
     * Contar turmas ativas
     */
    @Transactional(readOnly = true)
    public long contarAtivas(UUID teacherId) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return turmaRepository.countAtivasByTeacher(teacher);
    }
    
    /**
     * Turmas com aula hoje
     */
    @Transactional(readOnly = true)
    public List<TurmaResponse> turmasComAulaHoje(UUID teacherId) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return turmaRepository.findTurmasComAulaHoje(teacher)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    // ============================================
    // UTILITÁRIOS
    // ============================================
    
    private void verificarPropriedade(UUID teacherId, UUID turmaTeacherId) {
        if (!teacherId.equals(turmaTeacherId)) {
            throw new UnauthorizedException("Você não tem permissão para acessar esta turma");
        }
    }
    
    private TurmaResponse mapToResponse(Turma turma) {
        return TurmaResponse.builder()
            .id(turma.getId())
            .nome(turma.getNome())
            .descricao(turma.getDescricao())
            .horario(turma.getHorario())
            .proximaAula(turma.getProximaAula())
            .mediaTurma(turma.getMediaTurma())
            .totalAlunos(turma.getTotalAlunos())
            .status(turma.getStatus())
            .criadoEm(turma.getCriadoEm())
            .atualizadoEm(turma.getAtualizadoEm())
            .build();
    }
}
