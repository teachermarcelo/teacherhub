package com.teacherdash.service;

import com.teacherdash.dto.AlunoRequest;
import com.teacherdash.dto.AlunoResponse;
import com.teacherdash.entity.Aluno;
import com.teacherdash.entity.TeacherUser;
import com.teacherdash.repository.AlunoRepository;
import com.teacherdash.repository.TeacherUserRepository;
import com.teacherdash.exception.ResourceNotFoundException;
import com.teacherdash.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AlunoService {
    
    private final AlunoRepository alunoRepository;
    private final TeacherUserRepository teacherUserRepository;
    
    // ============================================
    // CRUD BÁSICO
    // ============================================
    
    /**
     * Criar novo aluno
     */
    public AlunoResponse criar(UUID teacherId, AlunoRequest request) {
        log.info("Criando novo aluno para professor: {}", teacherId);
        
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        Aluno aluno = Aluno.builder()
            .teacherUser(teacher)
            .nome(request.getNome())
            .email(request.getEmail())
            .telefone(request.getTelefone())
            .fotoUrl(request.getFotoUrl())
            .status(request.getStatus() != null ? request.getStatus() : "ativo")
            .build();
        
        Aluno saved = alunoRepository.save(aluno);
        log.info("Aluno criado: {} para professor: {}", saved.getId(), teacherId);
        
        return mapToResponse(saved);
    }
    
    /**
     * Buscar aluno por ID (com verificação de propriedade)
     */
    @Transactional(readOnly = true)
    public AlunoResponse buscarPorId(UUID teacherId, UUID alunoId) {
        Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado: " + alunoId));
        
        verificarPropriedade(teacherId, aluno.getTeacherUser().getId());
        return mapToResponse(aluno);
    }
    
    /**
     * Listar alunos do professor (paginado)
     */
    @Transactional(readOnly = true)
    public Page<AlunoResponse> listarPorProfessor(UUID teacherId, Pageable pageable) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return alunoRepository.findByTeacherUser(teacher, pageable)
            .map(this::mapToResponse);
    }
    
    /**
     * Listar alunos ativos do professor
     */
    @Transactional(readOnly = true)
    public List<AlunoResponse> listarAtivos(UUID teacherId) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return alunoRepository.findAtivosByTeacher(teacher)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Atualizar aluno
     */
    public AlunoResponse atualizar(UUID teacherId, UUID alunoId, AlunoRequest request) {
        log.info("Atualizando aluno: {} do professor: {}", alunoId, teacherId);
        
        Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado: " + alunoId));
        
        verificarPropriedade(teacherId, aluno.getTeacherUser().getId());
        
        if (request.getNome() != null) {
            aluno.setNome(request.getNome());
        }
        if (request.getEmail() != null) {
            aluno.setEmail(request.getEmail());
        }
        if (request.getTelefone() != null) {
            aluno.setTelefone(request.getTelefone());
        }
        if (request.getFotoUrl() != null) {
            aluno.setFotoUrl(request.getFotoUrl());
        }
        if (request.getStatus() != null) {
            aluno.setStatus(request.getStatus());
        }
        
        Aluno updated = alunoRepository.save(aluno);
        log.info("Aluno atualizado: {}", alunoId);
        
        return mapToResponse(updated);
    }
    
    /**
     * Deletar aluno
     */
    public void deletar(UUID teacherId, UUID alunoId) {
        log.info("Deletando aluno: {} do professor: {}", alunoId, teacherId);
        
        Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado: " + alunoId));
        
        verificarPropriedade(teacherId, aluno.getTeacherUser().getId());
        
        alunoRepository.delete(aluno);
        log.info("Aluno deletado: {}", alunoId);
    }
    
    // ============================================
    // OPERAÇÕES DE NEGÓCIO
    // ============================================
    
    /**
     * Buscar alunos por nome
     */
    @Transactional(readOnly = true)
    public List<AlunoResponse> buscarPorNome(UUID teacherId, String nome) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return alunoRepository.searchByNome(teacher, nome)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Contar alunos ativos
     */
    @Transactional(readOnly = true)
    public long contarAtivos(UUID teacherId) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return alunoRepository.countAtivosByTeacher(teacher);
    }
    
    /**
     * Marcar aluno como inativo
     */
    public AlunoResponse marcarInativo(UUID teacherId, UUID alunoId) {
        log.info("Marcando aluno como inativo: {}", alunoId);
        
        Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado: " + alunoId));
        
        verificarPropriedade(teacherId, aluno.getTeacherUser().getId());
        
        aluno.setStatus("inativo");
        Aluno updated = alunoRepository.save(aluno);
        
        log.info("Aluno marcado como inativo: {}", alunoId);
        return mapToResponse(updated);
    }
    
    /**
     * Reativar aluno
     */
    public AlunoResponse reativar(UUID teacherId, UUID alunoId) {
        log.info("Reativando aluno: {}", alunoId);
        
        Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado: " + alunoId));
        
        verificarPropriedade(teacherId, aluno.getTeacherUser().getId());
        
        aluno.setStatus("ativo");
        Aluno updated = alunoRepository.save(aluno);
        
        log.info("Aluno reativado: {}", alunoId);
        return mapToResponse(updated);
    }
    
    // ============================================
    // UTILITÁRIOS
    // ============================================
    
    private void verificarPropriedade(UUID teacherId, UUID alunoTeacherId) {
        if (!teacherId.equals(alunoTeacherId)) {
            throw new UnauthorizedException("Você não tem permissão para acessar este aluno");
        }
    }
    
    private AlunoResponse mapToResponse(Aluno aluno) {
        return AlunoResponse.builder()
            .id(aluno.getId())
            .nome(aluno.getNome())
            .email(aluno.getEmail())
            .telefone(aluno.getTelefone())
            .fotoUrl(aluno.getFotoUrl())
            .status(aluno.getStatus())
            .dataInscricao(aluno.getDataInscricao())
            .criadoEm(aluno.getCriadoEm())
            .atualizadoEm(aluno.getAtualizadoEm())
            .build();
    }
}
