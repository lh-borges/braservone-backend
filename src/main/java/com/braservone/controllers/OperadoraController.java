package com.braservone.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.braservone.models.Operadora;
import com.braservone.service.OperadoraService;

@RestController
@RequestMapping(value = "/api/operadora", produces = MediaType.APPLICATION_JSON_VALUE)
public class OperadoraController {

    private final OperadoraService opService;

    public OperadoraController(OperadoraService opService) {
        this.opService = opService;
    }

    // ===============================
    // MANTIDO (apenas simplificado)
    // ===============================
    @GetMapping
    public ResponseEntity<List<Operadora>> getAllOperadoras(Authentication authentication) {
        List<Operadora> lista = opService.getOperadoras();
        return ResponseEntity.ok(lista);
    }

    // ===============================
    // NOVOS ENDPOINTS
    // ===============================

    @GetMapping("/{id}")
    public ResponseEntity<Operadora> getById(@PathVariable Long id, Authentication authentication) {
        Operadora op = opService.getById(id, authentication);
        return ResponseEntity.ok(op);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Operadora> create(@RequestBody Operadora payload, Authentication authentication) {
        Operadora created = opService.create(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Operadora> update(@PathVariable Long id,
                                            @RequestBody Operadora payload,
                                            Authentication authentication) {
        Operadora updated = opService.update(id, payload, authentication);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/ativo")
    public ResponseEntity<Operadora> toggleAtivo(@PathVariable Long id,
                                                 @RequestParam boolean value,
                                                 Authentication authentication) {
        Operadora updated = opService.toggleAtivo(id, value);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        opService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
