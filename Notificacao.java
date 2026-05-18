package com.teacherdash.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "teacher_notificacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_user_id", nullable = false)
    private TeacherUser teacherUser;
    
    private String tipo; // fatura_vencida, aluno_inscrito, nota_baixa
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String mensagem;
    
    @Column(nullable = false)
    private Boolean lida = false;
    
    @Column(name = "data_leitura")
    private LocalDateTime dataLeitura;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
    
    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
    }
    
    // Helper: marca como lida
    public void marcarComoLida() {
        this.lida = true;
        this.dataLeitura = LocalDateTime.now();
    }
}
