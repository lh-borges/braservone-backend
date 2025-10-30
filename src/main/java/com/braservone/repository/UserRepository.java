package com.braservone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.braservone.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {  // String deve ser o tipo do ID de User

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
    
    Optional<User> findByUsername(String username);

    List<User> findUserByEmpresaId(Long idEmpresa);  // Aqui o tipo idEmpresa está correto

    @EntityGraph(attributePaths = "roles")
    @Query("select u from User u where u.username = :username")
    Optional<User> findByIdWithRoles(@Param("username") String username);
}
