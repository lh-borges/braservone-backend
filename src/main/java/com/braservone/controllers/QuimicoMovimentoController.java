package com.braservone.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.braservone.enums.TipoMovimento;
import com.braservone.enums.TipoQuimico;
import com.braservone.models.QuimicoMovimento;
import com.braservone.service.QuimicoMovimentoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/movimentosquimicos")
@Validated
public class QuimicoMovimentoController {

    private final QuimicoMovimentoService movimentoService;

    public QuimicoMovimentoController(QuimicoMovimentoService movimentoService) {
        this.movimentoService = movimentoService;
    }

    // DTO local
    public static class RegistrarMovimentoRequest {
        @NotNull private Long quimicoCodigo;
        @NotBlank private String pocoCodigoAnp;
        @NotNull private TipoMovimento tipo;
        @NotNull @DecimalMin("0.000001") private BigDecimal quantidade;

        public Long getQuimicoCodigo() { return quimicoCodigo; }
        public void setQuimicoCodigo(Long v) { this.quimicoCodigo = v; }
        public String getPocoCodigoAnp() { return pocoCodigoAnp; }
        public void setPocoCodigoAnp(String v) { this.pocoCodigoAnp = v; }
        public TipoMovimento getTipo() { return tipo; }
        public void setTipo(TipoMovimento v) { this.tipo = v; }
        public BigDecimal getQuantidade() { return quantidade; }
        public void setQuantidade(BigDecimal v) { this.quantidade = v; }
    }

    /** Registrar movimento */
    @PostMapping
    public ResponseEntity<QuimicoMovimento> registrar(@Valid @RequestBody RegistrarMovimentoRequest req) {
        var salvo = movimentoService.registrarPorCodigoAnp(
            req.getQuimicoCodigo(),
            req.getPocoCodigoAnp(),
            req.getTipo(),
            req.getQuantidade()
        );
        return ResponseEntity.ok(salvo);
    }

    /** Listar TODOS (com fetch no service) */
    @GetMapping
    public ResponseEntity<List<QuimicoMovimento>> listarMovimentoQuimicos() {
        var lista = movimentoService.listarTodos();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    /** Por POÇO */
    @GetMapping("/poco/{codigoAnp}")
    public ResponseEntity<List<QuimicoMovimento>> listarPorPocoCodigoAnp(@PathVariable String codigoAnp) {
        var lista = movimentoService.listarPorPocoCodigoAnp(codigoAnp);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    /** Por QUÍMICO */
    @GetMapping("/quimico/{quimicoCodigo}")
    public ResponseEntity<List<QuimicoMovimento>> listarPorQuimico(@PathVariable Long quimicoCodigo) {
        var lista = movimentoService.listarPorQuimico(quimicoCodigo);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    /** Por TIPO DE QUÍMICO */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<QuimicoMovimento>> listarPorTipo(@PathVariable TipoQuimico tipo) {
        var lista = movimentoService.listarPorTipo(tipo);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }
}
