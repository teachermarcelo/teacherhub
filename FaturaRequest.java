package com.teacherdash.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaturaRequest {
    
    @NotNull(message = "ID do aluno não pode ser nulo")
    private UUID alunoId;
    
    @NotNull(message = "ID da turma não pode ser nulo")
    private UUID turmaId;
    
    @NotNull(message = "Valor não pode ser nulo")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que 0")
    @Digits(integer = 10, fraction = 2, message = "Valor inválido")
    private BigDecimal valor;
    
    @NotBlank(message = "Descrição não pode estar vazia")
    @Size(min = 5, max = 255, message = "Descrição deve ter entre 5 e 255 caracteres")
    private String descricao;
    
    @NotNull(message = "Data de vencimento não pode ser nula")
    @FutureOrPresent(message = "Data de vencimento deve ser no futuro")
    private LocalDate dataVencimento;
    
    private String status; // pendente, pago, atrasado, cancelado
    
    private String metodoPagamento; // pix, boleto, cartao
    
    private String chavePix;
    
    @Size(max = 500, message = "Observação não pode exceder 500 caracteres")
    private String observacao;
}
