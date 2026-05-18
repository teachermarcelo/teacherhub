package com.teacherdash.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtividadeRequest {
    
    @NotNull(message = "ID da turma não pode ser nulo")
    private UUID turmaId;
    
    @NotBlank(message = "Título não pode estar vazio")
    @Size(min = 5, max = 255, message = "Título deve ter entre 5 e 255 caracteres")
    private String titulo;
    
    @Size(max = 1000, message = "Descrição não pode exceder 1000 caracteres")
    private String descricao;
    
    @Pattern(regexp = "^(exercicio|prova|trabalho|quiz)$", message = "Tipo inválido")
    private String tipo;
    
    @NotNull(message = "Data de entrega não pode ser nula")
    @FutureOrPresent(message = "Data de entrega deve ser no futuro")
    private LocalDateTime dataEntrega;
    
    @DecimalMin(value = "0.01", message = "Valor máximo deve ser maior que 0")
    @Digits(integer = 3, fraction = 2, message = "Valor máximo inválido")
    private BigDecimal valorMaximo;
    
    private String status; // ativa, corrigindo, finalizada
}
