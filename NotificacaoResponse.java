package com.teacherdash.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacaoResponse {
    
    private UUID id;
    
    private String tipo;
    
    private String titulo;
    
    private String mensagem;
    
    private Boolean lida;
    
    private LocalDateTime dataLeitura;
    
    private LocalDateTime criadoEm;
}
