package com.teacherdash.repository;

import com.teacherdash.entity.ConfiguracaoUsuario;
import com.teacherdash.entity.TeacherUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfiguracaoUsuarioRepository extends JpaRepository<ConfiguracaoUsuario, UUID> {
    
    // Buscar configurações por professor
    Optional<ConfiguracaoUsuario> findByTeacherUser(TeacherUser teacherUser);
}
