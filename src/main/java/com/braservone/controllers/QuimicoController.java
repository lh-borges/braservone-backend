package com.braservone.controllers;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.braservone.enums.StatusQuimicos;
import com.braservone.models.Quimico;
import com.braservone.service.QuimicoService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/quimicos")
@Validated
public class QuimicoController {

    private final QuimicoService quimicoService;

    public QuimicoController(QuimicoService quimicoService) {
        this.quimicoService = quimicoService;
    }

    @GetMapping
    public ResponseEntity<List<Quimico>> listar() {
        // ideal: service.listar() já buscar com fornecedor carregado (veja nota abaixo)
        List<Quimico> entidades = quimicoService.listar();
        return ResponseEntity.ok(entidades);
    }
    @GetMapping("/ativos")
    public ResponseEntity<List<Quimico>> listarAtivos() {
        // ideal: service.listar() já buscar com fornecedor carregado (veja nota abaixo)
        List<Quimico> entidades = quimicoService.listarPorStatus(StatusQuimicos.ATIVO);
        return ResponseEntity.ok(entidades);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Quimico> buscar(@PathVariable Long codigo) {
        Quimico entidade = quimicoService.buscar(codigo); // lança 404 no service se não achar
        return ResponseEntity.ok(entidade);
    }

    @GetMapping("/{codigo}/estoque-atual")
    public ResponseEntity<BigDecimal> estoqueAtual(@PathVariable Long codigo) {
        return ResponseEntity.ok(quimicoService.estoqueAtual(codigo));
    }

    @PostMapping
    public ResponseEntity<Quimico> criar(@Valid @RequestBody Quimico payload) {
        Quimico salvo = quimicoService.criar(payload);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(salvo.getCodigo())
                .toUri();

        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Quimico> atualizar(@PathVariable Long codigo,
                                             @Valid @RequestBody Quimico payload) {
        if (payload.getCodigo() != null && !payload.getCodigo().equals(codigo)) {
            return ResponseEntity.badRequest().build();
        }
        Quimico atualizado = quimicoService.atualizar(codigo, payload);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> remover(@PathVariable Long codigo) {
        quimicoService.excluir(codigo);
        return ResponseEntity.noContent().build();
    }
}
