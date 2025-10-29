// com/projetopetroleo/repository/OperacaoRepository.java
package com.braservone.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.braservone.models.Operacao;

public interface OperacaoRepository extends JpaRepository<Operacao, Long> {

  @EntityGraph(attributePaths = {"poco","operadora"})
  @Override
  Page<Operacao> findAll(Pageable pageable);
  boolean existsByPoco_CodigoAnpAndDataInicioAndNomeOperacaoIgnoreCase(
	      String codigoAnp, LocalDateTime dataInicio, String nomeOperacao);

	  boolean existsByPoco_CodigoAnpAndDataInicioAndNomeOperacaoIgnoreCaseAndIdNot(
	      String codigoAnp, LocalDateTime dataInicio, String nomeOperacao, Long id);

  @EntityGraph(attributePaths = {"poco","operadora"})
  @Query("""
    select o from Operacao o
     where (:status is null or o.status = :status)
       and (:operadoraId is null or o.operadora.id = :operadoraId)
       and (:pocoCodigoAnp is null or o.poco.codigoAnp = :pocoCodigoAnp)
  """)
  Page<Operacao> search(Boolean status, Long operadoraId, String pocoCodigoAnp, Pageable pageable);

  @EntityGraph(attributePaths = {"poco","operadora"})
  Optional<Operacao> findById(Long id);
  
  
}
