package com.teacherdash.dto;

import lombok.*;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
import java.util.List;

// ============================================
// RESPOSTA GENÉRICA (Sucesso)
// ============================================
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    
    private boolean sucesso;
    
    private String mensagem;
    
    private T dados;
    
    private LocalDateTime timestamp;
    
    public ApiResponse(boolean sucesso, String mensagem, T dados) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.dados = dados;
        this.timestamp = LocalDateTime.now();
    }
}

// ============================================
// RESPOSTA PAGINADA
// ============================================
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    
    private List<T> conteudo;
    
    private int pagina;
    
    private int tamanhoPagina;
    
    private long total;
    
    private int totalPaginas;
    
    private boolean ultima;
    
    private boolean primeira;
    
    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
            .conteudo(page.getContent())
            .pagina(page.getNumber())
            .tamanhoPagina(page.getSize())
            .total(page.getTotalElements())
            .totalPaginas(page.getTotalPages())
            .ultima(page.isLast())
            .primeira(page.isFirst())
            .build();
    }
}

// ============================================
// RESPOSTA DE ERRO
// ============================================
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    
    private int status;
    
    private String erro;
    
    private String mensagem;
    
    private String caminho;
    
    private LocalDateTime timestamp;
    
    private List<FieldError> errosCampo;
    
    public ErrorResponse(int status, String erro, String mensagem) {
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.timestamp = LocalDateTime.now();
    }
}

// ============================================
// ERRO DE CAMPO (Validação)
// ============================================
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldError {
    
    private String campo;
    
    private String mensagem;
    
    private Object valor;
}

// ============================================
// RESPOSTA DE LOGIN/AUTENTICAÇÃO
// ============================================
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    
    private String token;
    
    private String tipo = "Bearer";
    
    private Long expiresIn;
    
    private TeacherUserResponse usuario;
}

// ============================================
// REQUISIÇÃO DE LOGIN
// ============================================
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @org.hibernate.validator.constraints.NotBlank(message = "Email não pode estar vazio")
    @jakarta.validation.constraints.Email(message = "Email inválido")
    private String email;
    
    @org.hibernate.validator.constraints.NotBlank(message = "Senha não pode estar vazia")
    @jakarta.validation.constraints.Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;
}

// ============================================
// REQUISIÇÃO DE REGISTRO
// ============================================
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @jakarta.validation.constraints.NotBlank(message = "Email não pode estar vazio")
    @jakarta.validation.constraints.Email(message = "Email inválido")
    private String email;
    
    @jakarta.validation.constraints.NotBlank(message = "Nome não pode estar vazio")
    @jakarta.validation.constraints.Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String nome;
    
    @jakarta.validation.constraints.NotBlank(message = "Senha não pode estar vazia")
    @jakarta.validation.constraints.Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    private String senha;
    
    private String disciplina;
}
