package com.teacherdash.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlunoResponse {
    
    private UUID id;
    
    private String nome;
    
    private String email;
    
    private String telefone;
    
    private String fotoUrl;
    
    private String status;
    
    private LocalDateTime dataInscricao;
    
    private LocalDateTime criadoEm;
    
    private LocalDateTime atualizadoEm;
}
