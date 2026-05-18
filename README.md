# TeacherDash Backend 🎓

API REST completa para gerenciamento de professores, alunos, turmas e pagamentos.

## 📋 Stack Técnico

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA + Hibernate**
- **PostgreSQL (Supabase)**
- **JWT (JJWT)**
- **Spring Security**
- **Maven**

## 📦 Estrutura do Projeto

```
src/main/java/com/teacherdash/
├── controller/          # REST Controllers (40+ endpoints)
│   ├── AuthController
│   ├── TeacherUserController
│   ├── AlunoController
│   ├── TurmaController
│   └── FaturaController
├── service/             # Lógica de negócio
│   ├── AuthService
│   ├── TeacherUserService
│   ├── AlunoService
│   ├── TurmaService
│   └── FaturaService
├── repository/          # Data Access (JPA)
│   ├── TeacherUserRepository
│   ├── AlunoRepository
│   ├── TurmaRepository
│   ├── InscricaoRepository
│   ├── FaturaRepository
│   ├── AtividadeRepository
│   ├── SubmissaoRepository
│   ├── NotificacaoRepository
│   └── ConfiguracaoUsuarioRepository
├── entity/              # JPA Entities
│   ├── TeacherUser
│   ├── Aluno
│   ├── Turma
│   ├── Inscricao
│   ├── Fatura
│   ├── Atividade
│   ├── Submissao
│   ├── Notificacao
│   └── ConfiguracaoUsuario
├── dto/                 # Data Transfer Objects
│   ├── Request classes (TeacherUserRequest, etc)
│   ├── Response classes (TeacherUserResponse, etc)
│   └── Generic classes (ApiResponse, PageResponse, ErrorResponse, etc)
├── exception/           # Custom Exceptions
│   ├── TeacherDashException
│   ├── ResourceNotFoundException
│   ├── DuplicateResourceException
│   ├── UnauthorizedException
│   ├── ForbiddenException
│   ├── BadRequestException
│   ├── ConflictException
│   ├── InvalidOperationException
│   └── GlobalExceptionHandler
├── security/            # Segurança e JWT
│   ├── JwtTokenProvider
│   └── JwtAuthenticationFilter
├── config/              # Configurações
│   └── SecurityConfig
└── TeacherDashApplication # Main class
```

## 🚀 Instalação e Setup

### 1. Pré-requisitos
- Java 17 ou superior
- Maven 3.8.1 ou superior
- PostgreSQL 12+ (ou Supabase)

### 2. Clonar o repositório
```bash
git clone https://github.com/teacheramerica/teacherhub.git
cd teacherdash-backend
```

### 3. Variáveis de Ambiente
Criar arquivo `.env` na raiz do projeto:
```
# Banco de Dados
DB_URL=jdbc:postgresql://seu-host:5432/teacherdash
DB_USERNAME=seu-usuario
DB_PASSWORD=sua-senha

# JWT
JWT_SECRET=sua-chave-secreta-muito-longa-e-complexa-para-producao
JWT_EXPIRATION=86400000

# Supabase (opcional)
SUPABASE_URL=https://seu-projeto.supabase.co
SUPABASE_KEY=sua-chave-anonima
SUPABASE_JWT_SECRET=seu-jwt-secret

# Servidor
SERVER_PORT=8080

# Logging
SHOW_SQL=false
```

### 4. Executar migração do banco
```bash
# O Hibernate vai criar as tabelas automaticamente
# Ou execute o SQL manualmente em application.yml (ddl-auto: validate)
```

### 5. Build e Run
```bash
# Build
mvn clean package -DskipTests

# Run (dev)
mvn spring-boot:run

# Run (jar)
java -jar target/teacherdash-backend-1.0.0.jar
```

A aplicação estará rodando em `http://localhost:8080`

## 📚 API Endpoints

### Autenticação
- `POST /api/v1/auth/register` - Registrar novo professor
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Renovar token
- `POST /api/v1/auth/logout` - Logout
- `GET /api/v1/auth/verify` - Verificar token
- `POST /api/v1/auth/forgot-password` - Reset de senha
- `POST /api/v1/auth/reset-password` - Confirmar reset
- `POST /api/v1/auth/change-password` - Mudar senha

### Professores
- `GET/POST/PUT/DELETE /api/v1/professores` - CRUD
- `GET /api/v1/professores/me` - Meu perfil
- `PUT /api/v1/professores/{id}/pix/ativar` - Ativar PIX
- `PUT /api/v1/professores/{id}/plano` - Upgrade plano

### Alunos
- `GET/POST/PUT/DELETE /api/v1/alunos` - CRUD
- `GET /api/v1/alunos/filtro/ativos` - Listar ativos
- `PUT /api/v1/alunos/{id}/inativar` - Marcar inativo
- `GET /api/v1/alunos/relatorio/contar-ativos` - Contar ativos

### Turmas
- `GET/POST/PUT/DELETE /api/v1/turmas` - CRUD
- `GET /api/v1/turmas/filtro/ativas` - Listar ativas
- `GET /api/v1/turmas/filtro/aulas-hoje` - Aulas de hoje

### Faturas
- `GET/POST/PUT/DELETE /api/v1/faturas` - CRUD
- `PUT /api/v1/faturas/{id}/pagar` - Marcar como paga
- `GET /api/v1/faturas/filtro/pendentes` - Faturas pendentes
- `GET /api/v1/faturas/filtro/atrasadas` - Faturas atrasadas
- `GET /api/v1/faturas/relatorio/total-recebido` - Total recebido
- `GET /api/v1/faturas/relatorio/receita-mes` - Receita do mês

## 🔐 Autenticação

Todos os endpoints (exceto `/auth/register`, `/auth/login`) requerem JWT token.

### Enviar token no header:
```
Authorization: Bearer seu_jwt_token_aqui
```

### Exemplo de Login:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "professor@example.com",
    "senha": "senha_segura_123"
  }'
```

Resposta:
```json
{
  "sucesso": true,
  "mensagem": "Login realizado com sucesso",
  "dados": {
    "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
    "tipo": "Bearer",
    "expiresIn": 86400,
    "usuario": {
      "id": "uuid-do-professor",
      "email": "professor@example.com",
      "nome": "João Silva",
      "plano": "pro"
    }
  }
}
```

## 📖 Documentação Interativa

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

## 🧪 Testes

```bash
# Rodar testes
mvn test

# Com coverage
mvn test jacoco:report
```

## 🛠️ Desenvolvimento

### Adicionar novo endpoint
1. Criar método no `Service`
2. Expor via `Controller`
3. Adicionar validação no DTO
4. Testar com Swagger

### Perfis de execução
```bash
# Desenvolvimento
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Produção
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

## 📊 Database

### Tabelas principais
- `teacher_users` - Professores
- `teacher_alunos` - Alunos
- `teacher_turmas` - Turmas
- `teacher_inscricoes` - Aluno + Turma
- `teacher_faturas` - Pagamentos
- `teacher_atividades` - Exercícios/Provas
- `teacher_submissoes` - Entregas de atividades
- `teacher_notificacoes` - Notificações
- `teacher_configuracoes` - Preferências

### Views
- `teacher_vw_resumo_financeiro` - Relatório financeiro por professor
- `teacher_vw_desempenho_alunos` - Desempenho de alunos por turma

## 🔍 Monitoramento

- **Health:** http://localhost:8080/actuator/health
- **Metrics:** http://localhost:8080/actuator/metrics
- **Prometheus:** http://localhost:8080/actuator/prometheus

## 📝 Logging

Logs armazenados em `logs/teacherdash.log`

Níveis configuráveis via `application.yml`:
```yaml
logging:
  level:
    com.teacherdash: DEBUG
```

## 🚨 Tratamento de Erros

Todas as exceções retornam resposta padronizada:
```json
{
  "status": 404,
  "erro": "NOT_FOUND",
  "mensagem": "Recurso não encontrado",
  "caminho": "/api/v1/alunos/123",
  "timestamp": "2026-05-18T20:00:00"
}
```

Códigos HTTP utilizados:
- `200` - OK
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `409` - Conflict
- `500` - Internal Server Error

## 🤝 Contribuindo

1. Fork o repositório
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 Licença

Este projeto está sob licença MIT.

## 📞 Suporte

Para dúvidas ou problemas:
- Issues: https://github.com/teacheramerica/teacherhub/issues
- Email: suporte@teacherdash.com

---

**TeacherDash © 2026** - SaaS para Professores
