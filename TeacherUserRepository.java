package com.teacherdash.repository;

import com.teacherdash.entity.TeacherUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherUserRepository extends JpaRepository<TeacherUser, UUID> {
    
    // Buscar por email
    Optional<TeacherUser> findByEmail(String email);
    
    // Buscar por auth_id do Supabase
    Optional<TeacherUser> findByAuthId(UUID authId);
    
    // Verificar se email existe
    boolean existsByEmail(String email);
    
    // Buscar por plano
    @Query("SELECT t FROM TeacherUser t WHERE t.plano = :plano")
    java.util.List<TeacherUser> findByPlano(@Param("plano") String plano);
    
    // Buscar ativos com PIX
    @Query("SELECT t FROM TeacherUser t WHERE t.pixAtivo = true AND t.plano != 'free'")
    java.util.List<TeacherUser> findActivePixUsers();
}
