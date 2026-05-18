package com.teacherdash.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissaoRequest {
    
    @NotNull(message = "ID da atividade não pode ser nulo")
    private UUID atividadeId;
    
    @NotNull(message = "ID do aluno não pode ser nulo")
    private UUID alunoId;
    
    // Para aluno entregar:
    @Size(max = 500, message = "URL do arquivo não pode exceder 500 caracteres")
    private String arquivoUrl;
    
    @Size(max = 1000, message = "Comentário não pode exceder 1000 caracteres")
    private String comentario;
    
    // Para professor corrigir:
    @DecimalMin(value = "0", message = "Nota não pode ser negativa")
    @DecimalMax(value = "10", message = "Nota não pode exceder 10")
    @Digits(integer = 2, fraction = 1, message = "Nota inválida")
    private BigDecimal nota;
    
    @Size(max = 1000, message = "Feedback não pode exceder 1000 caracteres")
    private String feedback;
}
