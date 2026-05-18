package com.teacherdash.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlunoRequest {
    
    @NotBlank(message = "Nome não pode estar vazio")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String nome;
    
    @Email(message = "Email inválido")
    private String email;
    
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", message = "Telefone inválido")
    private String telefone;
    
    @Pattern(regexp = "^https?://.*", message = "URL da foto inválida")
    private String fotoUrl;
    
    private String status; // ativo, inativo, evadido
}
