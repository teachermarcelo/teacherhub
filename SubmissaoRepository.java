package com.teacherdash.repository;

import com.teacherdash.entity.Atividade;
import com.teacherdash.entity.Aluno;
import com.teacherdash.entity.Submissao;
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
public interface SubmissaoRepository extends JpaRepository<Submissao, UUID> {
    
    // Submissões de uma atividade
    Page<Submissao> findByAtividade(Atividade atividade, Pageable pageable);
    
    // Submissão de um aluno em uma atividade
    Optional<Submissao> findByAtividadeAndAluno(Atividade atividade, Aluno aluno);
    
    // Submissões de um aluno
    List<Submissao> findByAluno(Aluno aluno);
    
    // Submissões pendentes de correção
    @Query("SELECT s FROM Submissao s WHERE s.atividade = :atividade AND s.status = 'pendente'")
    List<Submissao> findPendentesByAtividade(@Param("atividade") Atividade atividade);
    
    // Submissões atrasadas (aluno entregou após prazo)
    @Query("SELECT s FROM Submissao s WHERE s.atividade = :atividade AND s.dataEntrega > s.atividade.dataEntrega")
    List<Submissao> findAtrasadasByAtividade(@Param("atividade") Atividade atividade);
    
    // Contar submissões não corrigidas
    @Query("SELECT COUNT(s) FROM Submissao s WHERE s.atividade = :atividade AND s.status = 'pendente'")
    long countPendentesByAtividade(@Param("atividade") Atividade atividade);
    
    // Média das notas de uma atividade
    @Query("SELECT AVG(s.nota) FROM Submissao s WHERE s.atividade = :atividade AND s.status = 'corrigida'")
    Double getMediaNotasAtividade(@Param("atividade") Atividade atividade);
    
    // Submissões mais recentes de um aluno
    @Query("SELECT s FROM Submissao s WHERE s.aluno = :aluno ORDER BY s.dataEntrega DESC")
    Page<Submissao> findRecentesByAluno(@Param("aluno") Aluno aluno, Pageable pageable);
}
