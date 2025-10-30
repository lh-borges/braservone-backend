package com.braservone.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.braservone.enums.StatusQuimicos;
import com.braservone.models.Quimico;

public interface QuimicoRepository extends JpaRepository<Quimico, Long> {

  @Query("select q from Quimico q join fetch q.fornecedor")
  List<Quimico> findAllFetchFornecedor();

  @Query("select q from Quimico q join fetch q.fornecedor where q.codigo = :codigo")
  Optional<Quimico> findOneFetchFornecedor(@Param("codigo") Long codigo);

  @Query("""
      select q from Quimico q
      join fetch q.fornecedor
      where q.statusQuimicos = com.braservone.enums.StatusQuimicos.ATIVO
      order by q.codigo desc
  """)
  List<Quimico> findAllAtivosFetchFornecedor();

  @Query("""
      select q from Quimico q
      join fetch q.fornecedor
      where q.codigo = :codigo
        and q.statusQuimicos = com.braservone.enums.StatusQuimicos.ATIVO
  """)
  Optional<Quimico> findAtivoByCodigoFetchFornecedor(@Param("codigo") Long codigo);

  // ===== Por status parametrizado =====
  @Query("""
      select q from Quimico q
      join fetch q.fornecedor
      where q.statusQuimicos = :status
      order by q.codigo desc
  """)
  List<Quimico> findAllByStatusFetchFornecedor(@Param("status") StatusQuimicos status);

  @Query(
    value = """
      select q from Quimico q
      join fetch q.fornecedor
      where q.statusQuimicos = :status
      order by q.codigo desc
    """,
    countQuery = """
      select count(q) from Quimico q
      where q.statusQuimicos = :status
    """
  )
  Page<Quimico> findPageByStatusFetchFornecedor(@Param("status") StatusQuimicos status, Pageable pageable);

  @EntityGraph(attributePaths = "fornecedor")
  List<Quimico> findByStatusQuimicos(StatusQuimicos status);

  @Query("""
      select
        coalesce(q.estoqueInicial, 0) +
        coalesce(
          sum(
            case
              when m.tipoMovimento = com.braservone.enums.TipoMovimento.ENTRADA
                then m.qntMovimentada
              else -m.qntMovimentada
            end
          ), 0
        )
      from Quimico q
      left join com.braservone.models.QuimicoMovimento m
             on m.quimico.codigo = q.codigo
      where q.codigo = :codigo
  """)
  Optional<BigDecimal> estoqueAtual(@Param("codigo") Long codigo);

  @Query("""
      select coalesce(
        sum(
          case
            when m.tipoMovimento = com.braservone.enums.TipoMovimento.ENTRADA
              then m.qntMovimentada
            else -m.qntMovimentada
          end
        ), 0
      )
      from com.braservone.models.QuimicoMovimento m
      where m.quimico.codigo = :codigo
  """)
  BigDecimal sumMovimentadoLiquido(@Param("codigo") Long codigo);
  
  @Query("""
		  select new com.braservone.DTO.EstoqueQuimicoPorTipoRegiaoDTO(
		      q.tipoQuimico,
		      q.estadoLocalArmazenamento,
		      coalesce(sum(
		        coalesce(q.estoqueInicial, 0) +
		        coalesce(
		          (
		            select sum(
		              case
		                when m2.tipoMovimento = com.braservone.enums.TipoMovimento.ENTRADA
		                  then m2.qntMovimentada
		                else -m2.qntMovimentada
		              end
		            )
		            from com.braservone.models.QuimicoMovimento m2
		            where m2.quimico.codigo = q.codigo
		          ), 0
		        )
		      ), 0)
		  )
		  from Quimico q
		  group by q.tipoQuimico, q.estadoLocalArmazenamento
		  order by q.tipoQuimico, q.estadoLocalArmazenamento
		""")
		List<com.braservone.DTO.EstoqueQuimicoPorTipoRegiaoDTO> listarEstoqueAgrupadoPorTipoEEstado();
  
}
