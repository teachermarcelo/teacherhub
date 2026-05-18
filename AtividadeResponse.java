package com.teacherdash.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtividadeResponse {
    
    private UUID id;
    
    private UUID turmaId;
    
    private String turmaNome;
    
    private String titulo;
    
    private String descricao;
    
    private String tipo;
    
    private LocalDateTime dataEntrega;
    
    private BigDecimal valorMaximo;
    
    private String status;
    
    private Boolean atrasada;
    
    private LocalDateTime criadoEm;
    
    private LocalDateTime atualizadoEm;
}
