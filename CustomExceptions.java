package com.teacherdash.exception;

/**
 * Exceção base customizada para a aplicação
 */
public class TeacherDashException extends RuntimeException {
    
    private final int statusCode;
    
    public TeacherDashException(String mensagem, int statusCode) {
        super(mensagem);
        this.statusCode = statusCode;
    }
    
    public TeacherDashException(String mensagem, Throwable causa, int statusCode) {
        super(mensagem, causa);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}

/**
 * ResourceNotFoundException - 404
 * Quando um recurso não é encontrado
 */
class ResourceNotFoundException extends TeacherDashException {
    
    public ResourceNotFoundException(String mensagem) {
        super(mensagem, 404);
    }
    
    public ResourceNotFoundException(String mensagem, Throwable causa) {
        super(mensagem, causa, 404);
    }
}

/**
 * DuplicateResourceException - 409
 * Quando tenta criar recurso duplicado (ex: email já existe)
 */
class DuplicateResourceException extends TeacherDashException {
    
    public DuplicateResourceException(String mensagem) {
        super(mensagem, 409);
    }
    
    public DuplicateResourceException(String mensagem, Throwable causa) {
        super(mensagem, causa, 409);
    }
}

/**
 * UnauthorizedException - 401
 * Quando usuário não está autenticado
 */
class UnauthorizedException extends TeacherDashException {
    
    public UnauthorizedException(String mensagem) {
        super(mensagem, 401);
    }
    
    public UnauthorizedException(String mensagem, Throwable causa) {
        super(mensagem, causa, 401);
    }
}

/**
 * ForbiddenException - 403
 * Quando usuário não tem permissão para acessar o recurso
 */
class ForbiddenException extends TeacherDashException {
    
    public ForbiddenException(String mensagem) {
        super(mensagem, 403);
    }
    
    public ForbiddenException(String mensagem, Throwable causa) {
        super(mensagem, causa, 403);
    }
}

/**
 * BadRequestException - 400
 * Quando requisição é inválida
 */
class BadRequestException extends TeacherDashException {
    
    public BadRequestException(String mensagem) {
        super(mensagem, 400);
    }
    
    public BadRequestException(String mensagem, Throwable causa) {
        super(mensagem, causa, 400);
    }
}

/**
 * ConflictException - 409
 * Quando há conflito na operação
 */
class ConflictException extends TeacherDashException {
    
    public ConflictException(String mensagem) {
        super(mensagem, 409);
    }
    
    public ConflictException(String mensagem, Throwable causa) {
        super(mensagem, causa, 409);
    }
}

/**
 * InvalidOperationException - 400
 * Quando operação é inválida (ex: marcar fatura paga quando já está paga)
 */
class InvalidOperationException extends TeacherDashException {
    
    public InvalidOperationException(String mensagem) {
        super(mensagem, 400);
    }
    
    public InvalidOperationException(String mensagem, Throwable causa) {
        super(mensagem, causa, 400);
    }
}
