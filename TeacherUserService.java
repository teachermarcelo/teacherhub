package com.teacherdash.service;

import com.teacherdash.dto.TeacherUserRequest;
import com.teacherdash.dto.TeacherUserResponse;
import com.teacherdash.entity.TeacherUser;
import com.teacherdash.entity.ConfiguracaoUsuario;
import com.teacherdash.repository.TeacherUserRepository;
import com.teacherdash.repository.ConfiguracaoUsuarioRepository;
import com.teacherdash.exception.ResourceNotFoundException;
import com.teacherdash.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TeacherUserService {
    
    private final TeacherUserRepository teacherUserRepository;
    private final ConfiguracaoUsuarioRepository configuracaoRepository;
    
    // ============================================
    // CRUD BÁSICO
    // ============================================
    
    /**
     * Criar novo professor
     */
    public TeacherUserResponse criar(TeacherUserRequest request) {
        log.info("Criando novo professor: {}", request.getEmail());
        
        if (teacherUserRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email já cadastrado: " + request.getEmail());
        }
        
        TeacherUser teacher = TeacherUser.builder()
            .email(request.getEmail())
            .nome(request.getNome())
            .disciplina(request.getDisciplina())
            .plano(request.getPlano() != null ? request.getPlano() : "free")
            .pixAtivo(request.getPixAtivo() != null ? request.getPixAtivo() : false)
            .pixKey(request.getPixKey())
            .numTurmas(0)
            .build();
        
        TeacherUser saved = teacherUserRepository.save(teacher);
        
        // Criar configurações padrão
        ConfiguracaoUsuario config = ConfiguracaoUsuario.builder()
            .teacherUser(saved)
            .build();
        configuracaoRepository.save(config);
        
        log.info("Professor criado com sucesso: {}", saved.getId());
        return mapToResponse(saved);
    }
    
    /**
     * Buscar professor por ID
     */
    @Transactional(readOnly = true)
    public TeacherUserResponse buscarPorId(UUID id) {
        TeacherUser teacher = teacherUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + id));
        return mapToResponse(teacher);
    }
    
    /**
     * Buscar professor por email
     */
    @Transactional(readOnly = true)
    public TeacherUserResponse buscarPorEmail(String email) {
        TeacherUser teacher = teacherUserRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + email));
        return mapToResponse(teacher);
    }
    
    /**
     * Buscar professor por authId (Supabase)
     */
    @Transactional(readOnly = true)
    public TeacherUserResponse buscarPorAuthId(UUID authId) {
        TeacherUser teacher = teacherUserRepository.findByAuthId(authId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com authId: " + authId));
        return mapToResponse(teacher);
    }
    
    /**
     * Atualizar professor
     */
    public TeacherUserResponse atualizar(UUID id, TeacherUserRequest request) {
        log.info("Atualizando professor: {}", id);
        
        TeacherUser teacher = teacherUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + id));
        
        if (request.getNome() != null) {
            teacher.setNome(request.getNome());
        }
        if (request.getDisciplina() != null) {
            teacher.setDisciplina(request.getDisciplina());
        }
        if (request.getPlano() != null) {
            teacher.setPlano(request.getPlano());
        }
        if (request.getPixAtivo() != null) {
            teacher.setPixAtivo(request.getPixAtivo());
        }
        if (request.getPixKey() != null) {
            teacher.setPixKey(request.getPixKey());
        }
        
        TeacherUser updated = teacherUserRepository.save(teacher);
        log.info("Professor atualizado: {}", id);
        
        return mapToResponse(updated);
    }
    
    /**
     * Deletar professor
     */
    public void deletar(UUID id) {
        log.info("Deletando professor: {}", id);
        
        TeacherUser teacher = teacherUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + id));
        
        teacherUserRepository.delete(teacher);
        log.info("Professor deletado: {}", id);
    }
    
    // ============================================
    // OPERAÇÕES DE NEGÓCIO
    // ============================================
    
    /**
     * Ativar/Desativar PIX
     */
    public TeacherUserResponse ativarPix(UUID id, String chavePix) {
        log.info("Ativando PIX para professor: {}", id);
        
        TeacherUser teacher = teacherUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + id));
        
        teacher.setPixAtivo(true);
        teacher.setPixKey(chavePix);
        
        TeacherUser updated = teacherUserRepository.save(teacher);
        log.info("PIX ativado para professor: {}", id);
        
        return mapToResponse(updated);
    }
    
    /**
     * Desativar PIX
     */
    public TeacherUserResponse desativarPix(UUID id) {
        log.info("Desativando PIX para professor: {}", id);
        
        TeacherUser teacher = teacherUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + id));
        
        teacher.setPixAtivo(false);
        teacher.setPixKey(null);
        
        TeacherUser updated = teacherUserRepository.save(teacher);
        log.info("PIX desativado para professor: {}", id);
        
        return mapToResponse(updated);
    }
    
    /**
     * Upgrade de plano
     */
    public TeacherUserResponse upgradePlano(UUID id, String novoPlano) {
        log.info("Fazendo upgrade de plano para professor: {}", id);
        
        TeacherUser teacher = teacherUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + id));
        
        if (!isPlanoValido(novoPlano)) {
            throw new IllegalArgumentException("Plano inválido: " + novoPlano);
        }
        
        teacher.setPlano(novoPlano);
        TeacherUser updated = teacherUserRepository.save(teacher);
        
        log.info("Plano atualizado para professor {}: {}", id, novoPlano);
        return mapToResponse(updated);
    }
    
    /**
     * Associar authId do Supabase
     */
    public void associarAuthId(UUID teacherId, UUID authId) {
        log.info("Associando authId para professor: {}", teacherId);
        
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        teacher.setAuthId(authId);
        teacherUserRepository.save(teacher);
        
        log.info("AuthId associado para professor: {}", teacherId);
    }
    
    // ============================================
    // UTILITÁRIOS
    // ============================================
    
    private boolean isPlanoValido(String plano) {
        return plano.equals("free") || plano.equals("pro") || plano.equals("enterprise");
    }
    
    private TeacherUserResponse mapToResponse(TeacherUser teacher) {
        return TeacherUserResponse.builder()
            .id(teacher.getId())
            .email(teacher.getEmail())
            .nome(teacher.getNome())
            .disciplina(teacher.getDisciplina())
            .numTurmas(teacher.getNumTurmas())
            .plano(teacher.getPlano())
            .pixAtivo(teacher.getPixAtivo())
            .criadoEm(teacher.getCriadoEm())
            .atualizadoEm(teacher.getAtualizadoEm())
            .build();
    }
}
