package com.teacherdash.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurmaRequest {
    
    @NotBlank(message = "Nome da turma não pode estar vazio")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String nome;
    
    @Size(max = 1000, message = "Descrição não pode exceder 1000 caracteres")
    private String descricao;
    
    @Pattern(regexp = "^\\d{1,2}h.*", message = "Horário inválido (ex: 19h de seg a sex)")
    private String horario;
    
    private LocalDateTime proximaAula;
    
    private String status; // ativa, pausada, finalizada
}
