package com.teacherdash.repository;

import com.teacherdash.entity.Atividade;
import com.teacherdash.entity.Turma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AtividadeRepository extends JpaRepository<Atividade, UUID> {
    
    // Atividades de uma turma
    Page<Atividade> findByTurma(Turma turma, Pageable pageable);
    
    // Atividades ativas de uma turma
    @Query("SELECT a FROM Atividade a WHERE a.turma = :turma AND a.status = 'ativa'")
    List<Atividade> findAtivasByTurma(@Param("turma") Turma turma);
    
    // Atividades atrasadas
    @Query("SELECT a FROM Atividade a WHERE a.turma = :turma AND a.status = 'ativa' AND a.dataEntrega < CURRENT_TIMESTAMP")
    List<Atividade> findAtrasadasByTurma(@Param("turma") Turma turma);
    
    // Próximas atividades (próximos 7 dias)
    @Query("SELECT a FROM Atividade a WHERE a.turma = :turma AND a.status = 'ativa' AND a.dataEntrega BETWEEN CURRENT_TIMESTAMP AND CURRENT_TIMESTAMP + 7 DAY ORDER BY a.dataEntrega")
    List<Atividade> findProximasByTurma(@Param("turma") Turma turma);
    
    // Atividades aguardando correção
    @Query("SELECT a FROM Atividade a WHERE a.turma = :turma AND a.status = 'corrigindo'")
    List<Atividade> findAguardandoCorrecao(@Param("turma") Turma turma);
    
    // Contar atividades ativas
    @Query("SELECT COUNT(a) FROM Atividade a WHERE a.turma = :turma AND a.status = 'ativa'")
    long countAtivasByTurma(@Param("turma") Turma turma);
}
