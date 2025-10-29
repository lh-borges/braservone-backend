// src/main/java/com/projetopetroleo/repository/RoleRepository.java
package com.braservone.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
// import org.springframework.stereotype.Repository; // opcional

import com.braservone.enums.ERole;
import com.braservone.models.Role;

// @Repository // opcional (JpaRepository já registra como bean)
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Busca exata (case sensitive conforme collation do banco)
    Optional<Role> findByName(String name);
    Optional<Role> findByName(ERole name);


    // Versão case-insensitive (útil para seeds)
    @Query("select r from Role r where upper(r.name) = upper(?1)")
    Optional<Role> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

    // Batch para seeds/validações
    List<Role> findAllByNameIn(Collection<String> names);

    // Listagem ordenada para UI/admin
    List<Role> findAllByOrderByNameAsc();
}
