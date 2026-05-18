package com.teacherdash.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherUserResponse {
    
    private UUID id;
    
    private String email;
    
    private String nome;
    
    private String disciplina;
    
    private Integer numTurmas;
    
    private String plano;
    
    private Boolean pixAtivo;
    
    private LocalDateTime criadoEm;
    
    private LocalDateTime atualizadoEm;
}
