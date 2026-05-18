package com.teacherdash.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "teacher_configuracoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracaoUsuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_user_id", nullable = false, unique = true)
    private TeacherUser teacherUser;
    
    @Column(name = "email_notificacoes")
    private Boolean emailNotificacoes = true;
    
    @Column(name = "notificacoes_fatura")
    private Boolean notificacoesFatura = true;
    
    @Column(name = "notificacoes_aluno")
    private Boolean notificacoesAluno = true;
    
    private String tema = "dark"; // dark, light
    
    private String idioma = "pt-BR";
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime atualizadoEm;
    
    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}
