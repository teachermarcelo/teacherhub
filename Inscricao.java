package com.teacherdash.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "teacher_inscricoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscricao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;
    
    @Column(name = "nota_atual")
    private BigDecimal notaAtual = BigDecimal.ZERO;
    
    private Integer frequencia = 0; // percentual
    
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
    
    // Unique constraint: (aluno_id, turma_id)
    @UniqueConstraint(columnNames = {"aluno_id", "turma_id"})
    public static class PrimaryConstraint {}
}
