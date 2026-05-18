package com.teacherdash.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "teacher_submissoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submissao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atividade_id", nullable = false)
    private Atividade atividade;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;
    
    @Column(name = "arquivo_url")
    private String arquivoUrl;
    
    @Column(columnDefinition = "TEXT")
    private String comentario;
    
    private BigDecimal nota;
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
    
    @Column(name = "data_entrega")
    private LocalDateTime dataEntrega;
    
    @Column(name = "data_correcao")
    private LocalDateTime dataCorrecao;
    
    @Column(nullable = false)
    private String status = "pendente"; // pendente, corrigida, rejeitada
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime atualizadoEm;
    
    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
        if (dataEntrega == null) {
            dataEntrega = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
    
    // Helper: marca como corrigida
    public void marcarComoCorrigida(BigDecimal nota, String feedback) {
        this.nota = nota;
        this.feedback = feedback;
        this.status = "corrigida";
        this.dataCorrecao = LocalDateTime.now();
    }
    
    // Helper: verifica se está atrasada
    public boolean isAtrasada() {
        return atividade != null && dataEntrega.isAfter(atividade.getDataEntrega());
    }
}
