package com.teacherdash.repository;

import com.teacherdash.entity.Notificacao;
import com.teacherdash.entity.TeacherUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID> {
    
    // Todas as notificações do professor
    Page<Notificacao> findByTeacherUser(TeacherUser teacherUser, Pageable pageable);
    
    // Notificações não lidas
    @Query("SELECT n FROM Notificacao n WHERE n.teacherUser = :teacher AND n.lida = false ORDER BY n.criadoEm DESC")
    List<Notificacao> findNaoLidasByTeacher(@Param("teacher") TeacherUser teacherUser);
    
    // Contar não lidas
    @Query("SELECT COUNT(n) FROM Notificacao n WHERE n.teacherUser = :teacher AND n.lida = false")
    long countNaoLidasByTeacher(@Param("teacher") TeacherUser teacherUser);
    
    // Notificações por tipo
    @Query("SELECT n FROM Notificacao n WHERE n.teacherUser = :teacher AND n.tipo = :tipo ORDER BY n.criadoEm DESC")
    List<Notificacao> findByTipo(@Param("teacher") TeacherUser teacherUser, @Param("tipo") String tipo);
    
    // Marcar todas como lidas
    @Query("UPDATE Notificacao n SET n.lida = true WHERE n.teacherUser = :teacher")
    void marcarTodasComoLidas(@Param("teacher") TeacherUser teacherUser);
    
    // Deletar notificações antigas (mais de 30 dias)
    @Query("DELETE FROM Notificacao n WHERE n.teacherUser = :teacher AND DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) > DATE(n.criadoEm)")
    void deletarAntigas(@Param("teacher") TeacherUser teacherUser);
}
