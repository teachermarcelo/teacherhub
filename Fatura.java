package com.teacherdash.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "teacher_faturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fatura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_user_id", nullable = false)
    private TeacherUser teacherUser;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;
    
    @Column(nullable = false)
    private BigDecimal valor;
    
    private String descricao; // Ex: "Mensalidade maio/2026"
    
    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;
    
    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;
    
    @Column(nullable = false)
    private String status = "pendente"; // pendente, pago, atrasado, cancelado
    
    @Column(name = "metodo_pagamento")
    private String metodoPagamento; // pix, boleto, cartao
    
    @Column(name = "chave_pix")
    private String chavePix;
    
    @Column(columnDefinition = "TEXT")
    private String observacao;
    
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
        return "pendente".equals(status) && LocalDate.now().isAfter(dataVencimento);
    }
    
    // Helper: marca como paga
    public void marcarComoPaga() {
        this.status = "pago";
        this.dataPagamento = LocalDateTime.now();
    }
}
