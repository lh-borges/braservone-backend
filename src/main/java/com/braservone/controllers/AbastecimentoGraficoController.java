// com.projetopetroleo.controller.AbastecimentoGraficoController
package com.braservone.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.braservone.DTO.AbastFiltroReq;
import com.braservone.DTO.DiaAggResp;
import com.braservone.services.AbastecimentoGraficoService;

@RestController
@RequestMapping("/api/abastecimentos/graficos")
public class AbastecimentoGraficoController {

  private final AbastecimentoGraficoService service;

  public AbastecimentoGraficoController(AbastecimentoGraficoService service) {
    this.service = service;
  }

  @GetMapping(value = "/diario", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<DiaAggResp> serieDiariaGet(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
      @RequestParam(required = false) String placa
  ) {
	  System.out.printf("DIARIO in=%s fim=%s placa=%s%n", inicio, fim, placa);
    return service.serieDiaria(new AbastFiltroReq(inicio, fim, placa));
  }

  @PostMapping(value = "/diario", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<DiaAggResp> serieDiariaPost(@RequestBody AbastFiltroReq req) {
    return service.serieDiaria(req);
  }

  @GetMapping(value = "/placas", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<String> placasNoPeriodo(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
  ) {
    return service.placasNoPeriodo(inicio, fim);
  }
}
