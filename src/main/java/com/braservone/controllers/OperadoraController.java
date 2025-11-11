package com.braservone.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.braservone.models.Operadora;
import com.braservone.services.OperadoraService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/operadora", produces = MediaType.APPLICATION_JSON_VALUE)
public class OperadoraController {

    private final OperadoraService opService;

    public OperadoraController(OperadoraService opService) {
        this.opService = opService;
    }

    // LISTAR TODAS
    @GetMapping
    public ResponseEntity<List<Operadora>> getAllOperadoras() {
        List<Operadora> lista = opService.getOperadoras();
        return ResponseEntity.ok(lista);
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Operadora> getById(@PathVariable Long id) {
        Operadora op = opService.getById(id);
        return ResponseEntity.ok(op);
    }

    // CRIAR
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Operadora> create(@Valid @RequestBody Operadora payload) {
        Operadora created = opService.create(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ATUALIZAR
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Operadora> update(@PathVariable Long id,
                                            @Valid @RequestBody Operadora payload) {
        Operadora updated = opService.update(id, payload);
        return ResponseEntity.ok(updated);
    }

    // ATIVAR / DESATIVAR
    @PatchMapping("/{id}/ativo")
    public ResponseEntity<Operadora> toggleAtivo(@PathVariable Long id,
                                                 @RequestParam boolean value) {
        Operadora updated = opService.toggleAtivo(id, value);
        return ResponseEntity.ok(updated);
    }

    // DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        opService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
