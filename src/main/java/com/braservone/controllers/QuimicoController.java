package com.braservone.controllers;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.braservone.DTO.EstoqueQuimicoPorTipoRegiaoDTO;
import com.braservone.enums.StatusQuimicos;
import com.braservone.models.Quimico;
import com.braservone.services.QuimicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/quimicos")
@Validated
public class QuimicoController {

    private final QuimicoService quimicoService;

    public QuimicoController(QuimicoService quimicoService) {
        this.quimicoService = quimicoService;
    }

    // ========================= READ =========================

    /** Lista todos (qualquer status) com fornecedor carregado. */
    @GetMapping
    public ResponseEntity<List<Quimico>> listar() {
        List<Quimico> entidades = quimicoService.listar();
        return ResponseEntity.ok(entidades);
    }

    /** Lista apenas ATIVOS (atalho semântico). */
    @GetMapping("/ativos")
    public ResponseEntity<List<Quimico>> listarAtivos() {
        List<Quimico> entidades = quimicoService.listarAtivos();
        return ResponseEntity.ok(entidades);
    }

    /** Lista por status específico (ATIVO/INATIVO/FINALIZADO). */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Quimico>> listarPorStatus(@PathVariable String status) {
        StatusQuimicos st = StatusQuimicos.valueOf(status.toUpperCase());
        return ResponseEntity.ok(quimicoService.listarPorStatus(st));
    }

    /** Lista leve para selects, cards e labels. */
    @GetMapping("/lite")
    public ResponseEntity<List<QuimicoService.QuimicoLite>> listarLite() {
        return ResponseEntity.ok(quimicoService.listarLite());
    }

    /** Busca por código. */
    @GetMapping("/{codigo}")
    public ResponseEntity<Quimico> buscar(@PathVariable Long codigo) {
        Quimico entidade = quimicoService.buscar(codigo);
        return ResponseEntity.ok(entidade);
    }

    /** Saldo atual de um químico. */
    @GetMapping("/{codigo}/estoque-atual")
    public ResponseEntity<BigDecimal> estoqueAtual(@PathVariable Long codigo) {
        return ResponseEntity.ok(quimicoService.estoqueAtual(codigo));
    }

    /** Estoque agrupado por tipo e estado (RN, AL, ...). */
    @GetMapping("/estoque-agrupado")
    public ResponseEntity<List<EstoqueQuimicoPorTipoRegiaoDTO>> listarEstoqueAgrupado() {
        var lista = quimicoService.listarEstoqueAgrupadoPorTipoEEstado();
        return ResponseEntity.ok(lista);
    }

    // ========================= CREATE =========================

    @PostMapping
    public ResponseEntity<Quimico> criar(@Valid @RequestBody Quimico payload) {
        Quimico salvo = quimicoService.criar(payload);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(salvo.getCodigo())
                .toUri();

        return ResponseEntity.created(location).body(salvo);
    }

    // ========================= UPDATE =========================

    /** PUT: atualização "completa" (respeita validações do service). */
    @PutMapping("/{codigo}")
    public ResponseEntity<Quimico> atualizar(@PathVariable Long codigo,
                                             @Valid @RequestBody Quimico payload) {
        if (payload.getCodigo() != null && !payload.getCodigo().equals(codigo)) {
            return ResponseEntity.badRequest().build();
        }
        Quimico atualizado = quimicoService.atualizar(codigo, payload);
        return ResponseEntity.ok(atualizado);
    }

    /** PATCH: atualização parcial (usa o mesmo service.atualizar com o "patch"). */
    @PatchMapping("/{codigo}")
    public ResponseEntity<Quimico> patch(@PathVariable Long codigo,
                                         @RequestBody Quimico patch) {
        Quimico atualizado = quimicoService.atualizar(codigo, patch);
        return ResponseEntity.ok(atualizado);
    }

    // ========================= DELETE =========================

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> remover(@PathVariable Long codigo) {
        quimicoService.excluir(codigo);
        return ResponseEntity.noContent().build();
    }
}
