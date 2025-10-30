// com.projetopetroleo.repository.QuimicoMovimentoRepository
package com.braservone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.braservone.enums.TipoQuimico;
import com.braservone.models.QuimicoMovimento;

public interface QuimicoMovimentoRepository extends JpaRepository<QuimicoMovimento, Long> {

    @Query("""
      select distinct m from QuimicoMovimento m
        left join fetch m.quimico q
        left join fetch q.fornecedor
        left join fetch m.poco
      order by m.criadoEm desc
    """)
    List<QuimicoMovimento> findAllFetch();

    @Query("""
      select distinct m from QuimicoMovimento m
        left join fetch m.quimico q
        left join fetch q.fornecedor
        left join fetch m.poco p
      where p.codigoAnp = :codigoAnp
      order by m.criadoEm desc
    """)
    List<QuimicoMovimento> findByPocoCodigoAnpFetch(@Param("codigoAnp") String codigoAnp);

    @Query("""
      select distinct m from QuimicoMovimento m
        left join fetch m.quimico q
        left join fetch q.fornecedor
        left join fetch m.poco
      where q.codigo = :quimicoCodigo
      order by m.criadoEm desc
    """)
    List<QuimicoMovimento> findByQuimicoCodigoFetch(@Param("quimicoCodigo") Long quimicoCodigo);

    @Query("""
      select distinct m from QuimicoMovimento m
        left join fetch m.quimico q
        left join fetch q.fornecedor
        left join fetch m.poco
      where q.tipoQuimico = :tipo
      order by m.criadoEm desc
    """)
    List<QuimicoMovimento> findByTipoQuimicoFetch(@Param("tipo") TipoQuimico tipo);

    @Query(value = """
      select m.*
        from quimico_movimento m
   left join quimico q on q.codigo      = m.quimico_codigo
   left join poco    p on p.codigo_anp  = m.poco_codigo_anp
      order by m.criado_em desc
    """, nativeQuery = true)
    List<QuimicoMovimento> findAllNative();

    @Query(value = """
      select m.*
        from quimico_movimento m
   left join quimico q on q.codigo      = m.quimico_codigo
   left join poco    p on p.codigo_anp  = m.poco_codigo_anp
       where p.codigo_anp = :codigoAnp
      order by m.criado_em desc
    """, nativeQuery = true)
    List<QuimicoMovimento> findByPocoNative(@Param("codigoAnp") String codigoAnp);

    @Query(value = """
      select m.*
        from quimico_movimento m
   left join quimico q on q.codigo      = m.quimico_codigo
   left join poco    p on p.codigo_anp  = m.poco_codigo_anp
       where q.codigo = :quimicoCodigo
      order by m.criado_em desc
    """, nativeQuery = true)
    List<QuimicoMovimento> findByQuimicoNative(@Param("quimicoCodigo") Long quimicoCodigo);

    @Query(value = """
      select m.*
        from quimico_movimento m
   left join quimico q on q.codigo      = m.quimico_codigo
   left join poco    p on p.codigo_anp  = m.poco_codigo_anp
       where q.tipo_quimico = :tipo
      order by m.criado_em desc
    """, nativeQuery = true)
    List<QuimicoMovimento> findByTipoQuimicoNative(@Param("tipo") String tipo);


    List<QuimicoMovimento> findAllByOrderByCriadoEmDesc();
    
    
}
