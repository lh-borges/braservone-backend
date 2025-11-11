// src/main/java/com/projetopetroleo/controllers/FornecedorController.java
package com.braservone.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.braservone.models.Fornecedor;
import com.braservone.services.FornecedorService;

@RestController
@RequestMapping("/api/fornecedores")
@Validated
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    // GET /api/fornecedores?q=nome
    @GetMapping
    public ResponseEntity<List<Fornecedor>> listar(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(fornecedorService.listar(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fornecedor> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(fornecedorService.buscar(id));
    }

    @PostMapping
    public ResponseEntity<Fornecedor> criar(@RequestBody Fornecedor payload) {
        Fornecedor salvo = fornecedorService.criar(payload);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(salvo.getId())
            .toUri();
        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fornecedor> atualizar(@PathVariable Long id,
                                                @RequestBody Fornecedor payload) {
        // Opcional: validar divergência de ID no body
        if (payload.getId() != null && !payload.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        Fornecedor atualizado = fornecedorService.atualizar(id, payload);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        fornecedorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
