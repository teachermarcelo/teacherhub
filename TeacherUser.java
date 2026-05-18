package com.teacherdash.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "teacher_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true)
    private UUID authId; // Relação com Supabase Auth
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String nome;
    
    private String disciplina;
    
    @Column(name = "num_turmas")
    private Integer numTurmas = 0;
    
    @Column(nullable = false)
    private String plano = "free"; // free, pro, enterprise
    
    @Column(name = "pix_ativo")
    private Boolean pixAtivo = false;
    
    @Column(name = "pix_key")
    private String pixKey;
    
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
