package com.teacherdash.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissaoResponse {
    
    private UUID id;
    
    private UUID atividadeId;
    
    private String atividadeTitulo;
    
    private UUID alunoId;
    
    private String alunoNome;
    
    private String arquivoUrl;
    
    private String comentario;
    
    private BigDecimal nota;
    
    private String feedback;
    
    private LocalDateTime dataEntrega;
    
    private LocalDateTime dataCorrecao;
    
    private String status;
    
    private Boolean atrasada;
    
    private LocalDateTime criadoEm;
    
    private LocalDateTime atualizadoEm;
}
