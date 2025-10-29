// com.projetopetroleo.repository.AbastecimentoRepository
package com.braservone.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.braservone.DTO.DiaSomaProj;
import com.braservone.models.Abastecimento;
import com.braservone.models.Veiculo;

public interface AbastecimentoRepository extends JpaRepository<Abastecimento, Long> {

  // seus existentes
  Page<Abastecimento> findByVeiculo(Veiculo veiculo, Pageable pageable);

  Page<Abastecimento> findByVeiculo_PlacaOrderByDataAbastecimentoDesc(
      String placa, Pageable pageable);

  // --- agregações diárias p/ gráficos ---
  @Query("""
      select 
         function('date', a.dataAbastecimento) as dia,
         sum(a.valorTotal)   as somaValorTotal,
         sum(a.distRodadaKm) as somaKm,
         sum(a.volumeLitros) as somaLitros,
         count(a)            as qtd
      from Abastecimento a
      where a.dataAbastecimento between :inicio and :fim
        and (:placa is null or :placa = '' or a.veiculo.placa = :placa)
      group by function('date', a.dataAbastecimento)
      order by dia asc
  """)
  List<DiaSomaProj> somasPorDia(
      @Param("inicio") LocalDateTime inicio,
      @Param("fim") LocalDateTime fim,
      @Param("placa") String placa
  );

  @Query("""
      select distinct v.placa
      from Abastecimento a
        join a.veiculo v
      where a.dataAbastecimento between :inicio and :fim
      order by v.placa
  """)
  List<String> listarPlacasNoPeriodo(
      @Param("inicio") LocalDateTime inicio,
      @Param("fim") LocalDateTime fim
  );
}
