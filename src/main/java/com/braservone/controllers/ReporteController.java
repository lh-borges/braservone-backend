package com.braservone.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.braservone.DTO.ReporteCreateDTO;
import com.braservone.DTO.ReporteResponseDTO;
import com.braservone.enums.Setor;
import com.braservone.enums.StatusReporte;
import com.braservone.models.Reporte;
import com.braservone.services.ReporteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Validated
@CrossOrigin // ajuste se precisar restringir origens
public class ReporteController {

    private final ReporteService service;

    // util: mapeia entidade -> DTO resposta
    private static ReporteResponseDTO toDto(Reporte r) {
        String placa = (r.getVeiculo() != null ? r.getVeiculo().getPlaca() : null);
        return new ReporteResponseDTO(
            r.getId(),
            r.getMensagem(),
            r.getMatricula(),
            r.getSetor(),
            r.getStatus(),
            placa,
            r.getDataHoraReporte()
        );
    }

    // ===== CRUD =====

    /** Cria um reporte (veículo opcional via placa) */
    @PostMapping
    public ResponseEntity<ReporteResponseDTO> create(@Valid @RequestBody ReporteCreateDTO body) {
        Reporte r = new Reporte();
        r.setMensagem(body.mensagem());
        r.setMatricula(body.matricula());
        r.setSetor(body.setor());
        r.setStatus(body.status()); // service dará default NOVO caso null

        Reporte saved = service.create(r, body.veiculoPlaca());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    /** Obtém um reporte por ID */
    @GetMapping("/{id}")
    public ReporteResponseDTO get(@PathVariable Long id) {
        return toDto(service.get(id));
    }

    /** Lista com filtros opcionais, incluindo placa do veículo */
    @GetMapping
    public List<ReporteResponseDTO> list(
            @RequestParam(required = false) StatusReporte status,
            @RequestParam(required = false) Setor setor,
            @RequestParam(required = false) String placa, // << filtro extra
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return service.list(status, setor, from, to, placa)
                      .stream().map(ReporteController::toDto).toList();
    }

    /** Atualiza apenas o status (via query param `value`) */
    @PatchMapping("/{id}/status")
    public ReporteResponseDTO updateStatus(@PathVariable Long id,
                                @RequestParam("value") StatusReporte novoStatus) {
        return toDto(service.updateStatus(id, novoStatus));
    }

    /** Associa ou desassocia um veículo (via query param `placa`; vazio/null desassocia) */
    @PatchMapping("/{id}/veiculo")
    public ReporteResponseDTO updateVeiculo(@PathVariable Long id,
                                            @RequestParam(required = false, name = "placa") String placa) {
        return toDto(service.updateVeiculo(id, placa));
    }

    /** Exclui um reporte */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
