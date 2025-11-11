// ObservacaoController.java
package com.braservone.controllers;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.braservone.models.Observacao;
import com.braservone.services.ObservacaoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reportes/{reporteId}/observacoes")
@RequiredArgsConstructor
@CrossOrigin // ajuste se precisar restringir origens
public class ObservacaoController {

    private final ObservacaoService service;

    // LISTA todas as observações de um reporte
    @GetMapping
    public List<ObservacaoResponse> list(@PathVariable Long reporteId) {
        return service.listarPorReporte(reporteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // OBTÉM uma observação específica de um reporte
    @GetMapping("/{observacaoId}")
    public ObservacaoResponse getOne(@PathVariable Long reporteId,
                                     @PathVariable Long observacaoId) {
        return service.listarPorReporte(reporteId)
                .stream()
                .filter(o -> observacaoId.equals(o.getId()))
                .findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Observação não encontrada para este reporte: idObs=" + observacaoId));
    }

    // CRIA uma nova observação
    @PostMapping
    public ResponseEntity<ObservacaoResponse> create(@PathVariable Long reporteId,
                                                     @Valid @RequestBody ObservacaoCreateRequest req) {
        Observacao nova = new Observacao();
        nova.setMensagem(req.mensagem());

        Observacao salva = service.adicionar(reporteId, nova);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salva.getId())
                .toUri();

        return ResponseEntity.created(location).body(toResponse(salva));
    }

    // ATUALIZA a mensagem de uma observação
    @PutMapping("/{observacaoId}")
    public ObservacaoResponse update(@PathVariable Long reporteId,
                                     @PathVariable Long observacaoId,
                                     @Valid @RequestBody ObservacaoUpdateRequest req) {
        Observacao atualizada = service.atualizarMensagem(reporteId, observacaoId, req.mensagem());
        return toResponse(atualizada);
    }

    // REMOVE uma observação do reporte
    @DeleteMapping("/{observacaoId}")
    public ResponseEntity<Void> delete(@PathVariable Long reporteId,
                                       @PathVariable Long observacaoId) {
        service.remover(reporteId, observacaoId);
        return ResponseEntity.noContent().build();
    }

    // ===== DTOs =====
    public record ObservacaoCreateRequest(@NotBlank String mensagem) {}
    public record ObservacaoUpdateRequest(@NotBlank String mensagem) {}
    public record ObservacaoResponse(Long id, String mensagem, LocalDateTime dataMensagem) {}

    private ObservacaoResponse toResponse(Observacao o) {
        return new ObservacaoResponse(o.getId(), o.getMensagem(), o.getDataMensagem());
    }
}
