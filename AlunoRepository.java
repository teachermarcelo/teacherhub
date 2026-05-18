package com.teacherdash.repository;

import com.teacherdash.entity.Aluno;
import com.teacherdash.entity.TeacherUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, UUID> {
    
    // Todos os alunos do professor
    Page<Aluno> findByTeacherUser(TeacherUser teacherUser, Pageable pageable);
    
    // Alunos ativos do professor
    @Query("SELECT a FROM Aluno a WHERE a.teacherUser = :teacher AND a.status = 'ativo'")
    List<Aluno> findAtivosByTeacher(@Param("teacher") TeacherUser teacherUser);
    
    // Buscar por email
    Optional<Aluno> findByEmailAndTeacherUser(String email, TeacherUser teacherUser);
    
    // Contar alunos ativos do professor
    @Query("SELECT COUNT(a) FROM Aluno a WHERE a.teacherUser = :teacher AND a.status = 'ativo'")
    long countAtivosByTeacher(@Param("teacher") TeacherUser teacherUser);
    
    // Buscar por nome (LIKE)
    @Query("SELECT a FROM Aluno a WHERE a.teacherUser = :teacher AND LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Aluno> searchByNome(@Param("teacher") TeacherUser teacherUser, @Param("nome") String nome);
}
