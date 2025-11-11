package com.braservone.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.braservone.enums.Setor;
import com.braservone.enums.StatusReporte;
import com.braservone.models.Reporte;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByStatus(StatusReporte status);
    List<Reporte> findBySetor(Setor setor);
    List<Reporte> findByDataHoraReporteBetween(LocalDateTime from, LocalDateTime to);

    @Query("""
           select r from Reporte r
           where (:status is null or r.status = :status)
             and (:setor  is null or r.setor  = :setor)
             and (:from   is null or r.dataHoraReporte >= :from)
             and (:to     is null or r.dataHoraReporte <= :to)
           order by r.dataHoraReporte desc
           """)
    List<Reporte> search(
            @Param("status") StatusReporte status,
            @Param("setor") Setor setor,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
