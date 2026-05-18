package com.teacherdash.repository;

import com.teacherdash.entity.Fatura;
import com.teacherdash.entity.TeacherUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface FaturaRepository extends JpaRepository<Fatura, UUID> {
    
    // Todas as faturas do professor
    Page<Fatura> findByTeacherUser(TeacherUser teacherUser, Pageable pageable);
    
    // Faturas por status
    @Query("SELECT f FROM Fatura f WHERE f.teacherUser = :teacher AND f.status = :status")
    Page<Fatura> findByStatusAndTeacher(@Param("teacher") TeacherUser teacherUser, 
                                        @Param("status") String status, Pageable pageable);
    
    // Faturas pendentes
    @Query("SELECT f FROM Fatura f WHERE f.teacherUser = :teacher AND f.status = 'pendente' ORDER BY f.dataVencimento")
    List<Fatura> findPendentesByTeacher(@Param("teacher") TeacherUser teacherUser);
    
    // Faturas atrasadas
    @Query("SELECT f FROM Fatura f WHERE f.teacherUser = :teacher AND f.status = 'pendente' AND f.dataVencimento < CURRENT_DATE")
    List<Fatura> findAtrasadasByTeacher(@Param("teacher") TeacherUser teacherUser);
    
    // Contar faturas pendentes
    @Query("SELECT COUNT(f) FROM Fatura f WHERE f.teacherUser = :teacher AND f.status = 'pendente'")
    long countPendentesByTeacher(@Param("teacher") TeacherUser teacherUser);
    
    // Total de receita (pago)
    @Query("SELECT COALESCE(SUM(f.valor), 0) FROM Fatura f WHERE f.teacherUser = :teacher AND f.status = 'pago'")
    BigDecimal getTotalRecebido(@Param("teacher") TeacherUser teacherUser);
    
    // Total pendente
    @Query("SELECT COALESCE(SUM(f.valor), 0) FROM Fatura f WHERE f.teacherUser = :teacher AND f.status = 'pendente'")
    BigDecimal getTotalPendente(@Param("teacher") TeacherUser teacherUser);
    
    // Receita no mês
    @Query("SELECT COALESCE(SUM(f.valor), 0) FROM Fatura f WHERE f.teacherUser = :teacher AND f.status = 'pago' AND YEAR(f.dataPagamento) = YEAR(CURRENT_DATE) AND MONTH(f.dataPagamento) = MONTH(CURRENT_DATE)")
    BigDecimal getReceitaMes(@Param("teacher") TeacherUser teacherUser);
    
    // Próximas faturas a vencer (próximos 7 dias)
    @Query("SELECT f FROM Fatura f WHERE f.teacherUser = :teacher AND f.status = 'pendente' AND f.dataVencimento BETWEEN CURRENT_DATE AND CURRENT_DATE + 7 ORDER BY f.dataVencimento")
    List<Fatura> findProximasAVencer(@Param("teacher") TeacherUser teacherUser);
}
