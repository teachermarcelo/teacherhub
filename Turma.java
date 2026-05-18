package com.teacherdash.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "teacher_turmas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Turma {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_user_id", nullable = false)
    private TeacherUser teacherUser;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    private String horario; // Ex: "19h de seg a sex"
    
    @Column(name = "proxima_aula")
    private LocalDateTime proximaAula;
    
    @Column(name = "media_turma")
    private BigDecimal mediaTurma = BigDecimal.ZERO;
    
    @Column(name = "total_alunos")
    private Integer totalAlunos = 0;
    
    @Column(nullable = false)
    private String status = "ativa"; // ativa, pausada, finalizada
    
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
