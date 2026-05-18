package com.teacherdash.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "teacher_atividades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Atividade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    private String tipo; // exercicio, prova, trabalho, quiz
    
    @Column(name = "data_entrega", nullable = false)
    private LocalDateTime dataEntrega;
    
    @Column(name = "valor_maximo")
    private BigDecimal valorMaximo = BigDecimal.TEN;
    
    @Column(nullable = false)
    private String status = "ativa"; // ativa, corrigindo, finalizada
    
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
    
    // Helper: verifica se está atrasada
    public boolean isAtrasada() {
        return "ativa".equals(status) && LocalDateTime.now().isAfter(dataEntrega);
    }
}
