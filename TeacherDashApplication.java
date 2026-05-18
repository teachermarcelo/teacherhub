package com.teacherdash;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "TeacherDash API",
        version = "1.0.0",
        description = "API REST para gerenciamento de professores, alunos, turmas e pagamentos",
        contact = @Contact(
            name = "TeacherDash",
            url = "https://teacherdash.com"
        )
    )
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT token para autenticação",
    in = SecuritySchemeIn.HEADER
)
public class TeacherDashApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TeacherDashApplication.class, args);
    }
    
    /**
     * PasswordEncoder bean (BCrypt com strength 12)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
