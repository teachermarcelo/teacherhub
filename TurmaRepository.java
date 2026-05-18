package com.teacherdash.repository;

import com.teacherdash.entity.Turma;
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
public interface TurmaRepository extends JpaRepository<Turma, UUID> {
    
    // Todas as turmas do professor
    Page<Turma> findByTeacherUser(TeacherUser teacherUser, Pageable pageable);
    
    // Turmas ativas do professor
    @Query("SELECT t FROM Turma t WHERE t.teacherUser = :teacher AND t.status = 'ativa'")
    List<Turma> findAtivasByTeacher(@Param("teacher") TeacherUser teacherUser);
    
    // Buscar turma por nome e professor
    @Query("SELECT t FROM Turma t WHERE t.teacherUser = :teacher AND LOWER(t.nome) = LOWER(:nome)")
    Optional<Turma> findByNomeAndTeacher(@Param("teacher") TeacherUser teacherUser, @Param("nome") String nome);
    
    // Contar turmas ativas
    @Query("SELECT COUNT(t) FROM Turma t WHERE t.teacherUser = :teacher AND t.status = 'ativa'")
    long countAtivasByTeacher(@Param("teacher") TeacherUser teacherUser);
    
    // Buscar por nome (LIKE)
    @Query("SELECT t FROM Turma t WHERE t.teacherUser = :teacher AND LOWER(t.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Turma> searchByNome(@Param("teacher") TeacherUser teacherUser, @Param("nome") String nome);
    
    // Turmas com próxima aula para hoje
    @Query("SELECT t FROM Turma t WHERE t.teacherUser = :teacher AND DATE(t.proximaAula) = CURRENT_DATE")
    List<Turma> findTurmasComAulaHoje(@Param("teacher") TeacherUser teacherUser);
}
