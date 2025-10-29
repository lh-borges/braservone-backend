// com.projetopetroleo.service.AbastecimentoGraficoService
package com.braservone.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.braservone.DTO.AbastFiltroReq;
import com.braservone.DTO.DiaAggResp;
import com.braservone.DTO.DiaSomaProj;
import com.braservone.repository.AbastecimentoRepository;

@Service
public class AbastecimentoGraficoService {

  private final AbastecimentoRepository repo;

  public AbastecimentoGraficoService(AbastecimentoRepository repo) {
    this.repo = repo;
  }

  public List<DiaAggResp> serieDiaria(AbastFiltroReq filtro) {
    LocalDate iniDate = filtro.inicio();
    LocalDate fimDate = filtro.fim();

    // conversão segura para abrangência do dia
    LocalDateTime ini = iniDate.atStartOfDay();
    LocalDateTime fim = fimDate.atTime(LocalTime.MAX);

    String placa = normalize(filtro.placa());

    List<DiaSomaProj> brutos = repo.somasPorDia(ini, fim, placa);

    return brutos.stream().map(p -> {
      BigDecimal custo = p.getSomaValorTotal() == null ? BigDecimal.ZERO : p.getSomaValorTotal();
      double km = nzd(p.getSomaKm());
      double litros = nzd(p.getSomaLitros());

      Double vlrMedioPorLitro = safeDiv(custo.doubleValue(), litros);
      Double kmPorLitro       = safeDiv(km, litros);
      Double rsPorKm          = safeDiv(custo.doubleValue(), km);

      return new DiaAggResp(
          p.getDia(),
          custo,
          km,
          litros,
          vlrMedioPorLitro,
          kmPorLitro,
          rsPorKm,
          p.getQtd()
      );
    }).toList();
  }

  public List<String> placasNoPeriodo(LocalDate inicio, LocalDate fim) {
    LocalDateTime ini = inicio.atStartOfDay();
    LocalDateTime end = fim.atTime(LocalTime.MAX);
    return repo.listarPlacasNoPeriodo(ini, end);
  }

  // helpers
  private static String normalize(String s) {
    return (s == null || s.isBlank()) ? null : s.trim();
  }
  private static double nzd(Double d) { return d == null ? 0d : d; }
  private static Double safeDiv(double a, double b) { return b == 0d ? null : a / b; }
}
