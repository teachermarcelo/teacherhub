package com.teacherdash.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "teacher_alunos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aluno {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_user_id", nullable = false)
    private TeacherUser teacherUser;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(unique = true)
    private String email;
    
    private String telefone;
    
    @Column(name = "foto_url")
    private String fotoUrl;
    
    @Column(nullable = false)
    private String status = "ativo"; // ativo, inativo, evadido
    
    @Column(name = "data_inscricao")
    private LocalDateTime dataInscricao;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime atualizadoEm;
    
    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
        dataInscricao = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}
