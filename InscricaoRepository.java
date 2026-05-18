package com.teacherdash.repository;

import com.teacherdash.entity.Aluno;
import com.teacherdash.entity.Inscricao;
import com.teacherdash.entity.Turma;
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
public interface InscricaoRepository extends JpaRepository<Inscricao, UUID> {
    
    // Buscar inscrição por aluno e turma
    Optional<Inscricao> findByAlunoAndTurma(Aluno aluno, Turma turma);
    
    // Todos os alunos de uma turma
    @Query("SELECT i FROM Inscricao i WHERE i.turma = :turma ORDER BY i.aluno.nome")
    Page<Inscricao> findByTurma(@Param("turma") Turma turma, Pageable pageable);
    
    List<Inscricao> findByAlunoId(UUID alunoId);
    
    List<Inscricao> findByTurmaId(UUID turmaId);
    
    // Média da turma
    @Query("SELECT AVG(i.notaAtual) FROM Inscricao i WHERE i.turma = :turma")
    Double getMediaTurma(@Param("turma") Turma turma);
    
    // Alunos com nota baixa (< 6)
    @Query("SELECT i FROM Inscricao i WHERE i.turma = :turma AND i.notaAtual < 6")
    List<Inscricao> findComNotaBaixa(@Param("turma") Turma turma);
    
    // Frequência média da turma
    @Query("SELECT AVG(i.frequencia) FROM Inscricao i WHERE i.turma = :turma")
    Double getFrequenciaMedia(@Param("turma") Turma turma);
    
    // Deletar inscrição
    void deleteByAlunoAndTurma(Aluno aluno, Turma turma);
}
