package com.teacherdash.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurmaResponse {
    
    private UUID id;
    
    private String nome;
    
    private String descricao;
    
    private String horario;
    
    private LocalDateTime proximaAula;
    
    private BigDecimal mediaTurma;
    
    private Integer totalAlunos;
    
    private String status;
    
    private LocalDateTime criadoEm;
    
    private LocalDateTime atualizadoEm;
}
