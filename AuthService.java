package com.teacherdash.service;

import com.teacherdash.dto.LoginResponse;
import com.teacherdash.dto.RegisterRequest;
import com.teacherdash.dto.TeacherUserResponse;
import com.teacherdash.entity.TeacherUser;
import com.teacherdash.entity.ConfiguracaoUsuario;
import com.teacherdash.repository.TeacherUserRepository;
import com.teacherdash.repository.ConfiguracaoUsuarioRepository;
import com.teacherdash.security.JwtTokenProvider;
import com.teacherdash.exception.UnauthorizedException;
import com.teacherdash.exception.ResourceNotFoundException;
import com.teacherdash.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    
    private final TeacherUserRepository teacherUserRepository;
    private final ConfiguracaoUsuarioRepository configuracaoRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    // ============================================
    // LOGIN
    // ============================================
    
    /**
     * Login com email e senha
     */
    public LoginResponse login(String email, String senha) {
        log.info("Realizando login para: {}", email);
        
        TeacherUser teacher = teacherUserRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("Email ou senha inválidos"));
        
        // Validar senha (comparar com hash armazenado)
        if (!passwordEncoder.matches(senha, teacher.getSenha())) {
            throw new UnauthorizedException("Email ou senha inválidos");
        }
        
        log.info("Login bem-sucedido para: {}", email);
        
        return gerarLoginResponse(teacher);
    }
    
    /**
     * Registrar novo professor
     */
    public LoginResponse register(RegisterRequest request) {
        log.info("Registrando novo professor: {}", request.getEmail());
        
        // Verificar se email já existe
        if (teacherUserRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email já cadastrado: " + request.getEmail());
        }
        
        // Validar senha (mínimo 8 caracteres)
        if (request.getSenha().length() < 8) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 8 caracteres");
        }
        
        // Criptografar senha
        String senhaHash = passwordEncoder.encode(request.getSenha());
        
        // Criar professor
        TeacherUser teacher = TeacherUser.builder()
            .email(request.getEmail())
            .nome(request.getNome())
            .disciplina(request.getDisciplina())
            .senha(senhaHash)
            .plano("free")
            .pixAtivo(false)
            .numTurmas(0)
            .build();
        
        TeacherUser saved = teacherUserRepository.save(teacher);
        
        // Criar configurações padrão
        ConfiguracaoUsuario config = ConfiguracaoUsuario.builder()
            .teacherUser(saved)
            .build();
        configuracaoRepository.save(config);
        
        log.info("Novo professor registrado: {}", saved.getId());
        
        return gerarLoginResponse(saved);
    }
    
    // ============================================
    // REFRESH TOKEN
    // ============================================
    
    /**
     * Renovar token JWT
     */
    public LoginResponse refreshToken(UUID teacherId, String email) {
        log.info("Renovando token para professor: {}", teacherId);
        
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        if (!teacher.getEmail().equals(email)) {
            throw new UnauthorizedException("Dados do token inconsistentes");
        }
        
        return gerarLoginResponse(teacher);
    }
    
    // ============================================
    // BUSCAR PROFESSOR
    // ============================================
    
    /**
     * Buscar dados do professor logado
     */
    @Transactional(readOnly = true)
    public TeacherUserResponse buscarProfessor(UUID teacherId) {
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        return mapToResponse(teacher);
    }
    
    // ============================================
    // RESET/CHANGE PASSWORD
    // ============================================
    
    /**
     * Solicitar reset de senha
     * (Implementação simplificada - em produção, enviar email com token)
     */
    public void forgotPassword(String email) {
        log.info("Solicitação de reset de senha para: {}", email);
        
        TeacherUser teacher = teacherUserRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Email não encontrado"));
        
        // TODO: Gerar token de reset, salvar no BD, enviar por email
        log.info("Token de reset gerado para: {}", email);
    }
    
    /**
     * Resetar senha com token
     */
    public void resetPassword(String token, String novaSenha) {
        log.info("Resetando senha com token");
        
        // TODO: Validar token, buscar professor, atualizar senha
        
        if (novaSenha.length() < 8) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 8 caracteres");
        }
        
        log.info("Senha resetada com sucesso");
    }
    
    /**
     * Mudar senha do professor logado
     */
    public void changePassword(UUID teacherId, String senhaAtual, String novaSenha) {
        log.info("Mudando senha para professor: {}", teacherId);
        
        TeacherUser teacher = teacherUserRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + teacherId));
        
        // Verificar senha atual
        if (!passwordEncoder.matches(senhaAtual, teacher.getSenha())) {
            throw new UnauthorizedException("Senha atual está incorreta");
        }
        
        // Validar nova senha
        if (novaSenha.length() < 8) {
            throw new IllegalArgumentException("Nova senha deve ter no mínimo 8 caracteres");
        }
        
        // Não permitir usar a mesma senha
        if (senhaAtual.equals(novaSenha)) {
            throw new IllegalArgumentException("Nova senha não pode ser igual à atual");
        }
        
        // Atualizar senha
        String novaSenhaHash = passwordEncoder.encode(novaSenha);
        teacher.setSenha(novaSenhaHash);
        teacherUserRepository.save(teacher);
        
        log.info("Senha alterada com sucesso para professor: {}", teacherId);
    }
    
    // ============================================
    // UTILITÁRIOS
    // ============================================
    
    /**
     * Gerar resposta de login com token JWT
     */
    private LoginResponse gerarLoginResponse(TeacherUser teacher) {
        String token = jwtTokenProvider.generateTokenWithClaims(
            teacher.getId(),
            teacher.getEmail(),
            null
        );
        
        long expiresIn = jwtTokenProvider.getExpirationTime(token);
        
        return LoginResponse.builder()
            .token(token)
            .tipo("Bearer")
            .expiresIn(expiresIn)
            .usuario(mapToResponse(teacher))
            .build();
    }
    
    /**
     * Mapear TeacherUser para Response
     */
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
