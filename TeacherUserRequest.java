package com.teacherdash.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherUserRequest {
    
    @NotBlank(message = "Email não pode estar vazio")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "Nome não pode estar vazio")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String nome;
    
    @Size(max = 100, message = "Disciplina não pode exceder 100 caracteres")
    private String disciplina;
    
    private String plano; // free, pro, enterprise
    
    private Boolean pixAtivo;
    
    @Pattern(regexp = "^[\\w\\-]+$", message = "Chave PIX inválida")
    private String pixKey;
}
