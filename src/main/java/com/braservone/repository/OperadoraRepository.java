// com/projetopetroleo/repository/OperadoraRepository.java
package com.braservone.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.braservone.models.Operadora;

public interface OperadoraRepository extends JpaRepository<Operadora, Long> {

    // Busca por sigla (sem empresa)
    Optional<Operadora> findBySiglasIgnoreCase(String siglas);

    // Busca paginada com filtros simples (sem empresa)
    @Query("""
           select o from Operadora o
           where (:ativo is null or o.ativo = :ativo)
             and (
                  :q is null
               or lower(o.nome)   like lower(concat('%', :q, '%'))
               or lower(o.siglas) like lower(concat('%', :q, '%'))
             )
           """)
    Page<Operadora> search(
            @Param("ativo") Boolean ativo,
            @Param("q") String q,
            Pageable pageable);

    // (Opcional) se quiser garantir ativo + id na mesma consulta
    Optional<Operadora> findByIdAndAtivo(Long id, Boolean ativo);
}
