package com.teacherdash.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaturaResponse {
    
    private UUID id;
    
    private UUID alunoId;
    
    private String alunoNome;
    
    private UUID turmaId;
    
    private String turmaNome;
    
    private BigDecimal valor;
    
    private String descricao;
    
    private LocalDate dataVencimento;
    
    private LocalDateTime dataPagamento;
    
    private String status;
    
    private String metodoPagamento;
    
    private String observacao;
    
    private Boolean atrasada;
    
    private LocalDateTime criadoEm;
    
    private LocalDateTime atualizadoEm;
}
