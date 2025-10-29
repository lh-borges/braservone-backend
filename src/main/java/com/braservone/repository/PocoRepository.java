// com/projetopetroleo/repository/PocoRepository.java
package com.braservone.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.braservone.models.Poco;

public interface PocoRepository extends JpaRepository<Poco, String> {

    // Busca um poço pelo codigoAnp (PK)
    Optional<Poco> findByCodigoAnp(String codigoAnp);

    // Deleta pelo codigoAnp e retorna quantos registros foram afetados
    long deleteByCodigoAnp(String codigoAnp);

    // (opcional) utilitário rápido
    boolean existsByCodigoAnp(String codigoAnp);
}
