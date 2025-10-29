// src/main/java/com/projetopetroleo/controllers/AbastecimentoController.java
package com.braservone.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.braservone.DTO.AbastecimentoCreateDTO;
import com.braservone.DTO.AbastecimentoResponseDTO;
import com.braservone.service.AbastecimentoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/abastecimentos")
@Validated
public class AbastecimentoController {

    private final AbastecimentoService service;

    public AbastecimentoController(AbastecimentoService service) {
        this.service = service;
    }

    // ========= READ =========

    /** GET /api/abastecimentos  -> lista geral paginada */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<AbastecimentoResponseDTO> listarTodos(Pageable pageable) {
        return service.listarTodos(pageable);
    }

    /** GET /api/abastecimentos/veiculo/{placa} -> lista por placa (paginado) */
    @GetMapping(value = "/veiculo/{placa}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<AbastecimentoResponseDTO> listarPorVeiculo(
            @PathVariable String placa,
            Pageable pageable
    ) {
        return service.listarPorVeiculo(placa, pageable);
    }

    /** GET /api/abastecimentos/{id} -> busca um */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AbastecimentoResponseDTO buscarUm(@PathVariable Long id) {
        return service.buscarUm(id);
    }

    // ========= CREATE =========

    /** POST /api/abastecimentos -> cria um registro */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AbastecimentoResponseDTO criar(@RequestBody @Valid AbastecimentoCreateDTO dto) {
        return service.criar(dto);
    }

    // ========= UPDATE =========

    /** PUT /api/abastecimentos/{id} -> atualiza um registro */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AbastecimentoResponseDTO atualizar(@PathVariable Long id,
                                              @RequestBody @Valid AbastecimentoCreateDTO dto) {
        return service.atualizar(id, dto);
    }

    // ========= DELETE =========

    /** DELETE /api/abastecimentos/{id} -> exclui um registro */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
