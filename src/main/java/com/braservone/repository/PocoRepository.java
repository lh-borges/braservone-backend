// com/projetopetroleo/repository/PocoRepository.java
package com.braservone.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.braservone.models.Poco;

public interface PocoRepository extends JpaRepository<Poco, String> {

    Optional<Poco> findByCodigoAnp(String codigoAnp);

    long deleteByCodigoAnp(String codigoAnp);

    boolean existsByCodigoAnp(String codigoAnp);
}
