// src/main/java/com/braservone/controllers/PocoController.java
package com.braservone.controllers;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.braservone.DTO.PocoDTO;
import com.braservone.models.Poco;
import com.braservone.service.PocoService;

@RestController
@RequestMapping("/api/pocos")
public class PocoController {

  private final PocoService pocoService;

  public PocoController(PocoService pocoService) {
    this.pocoService = pocoService;
  }

  // GET por código ANP
  @GetMapping(value = "/{codigoAnp}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getByCodigoAnp(@PathVariable String codigoAnp) {
    try {
      Poco poco = pocoService.getPocoByCodigoAnp(codigoAnp);
      return ResponseEntity.ok(poco);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Erro ao buscar poço: " + e.getMessage());
    }
  }

  // GET paginado em DTO
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> listPaged(
      @PageableDefault(size = 50)
      @SortDefault.SortDefaults({
          @SortDefault(sort = "codigoAnp", direction = Sort.Direction.ASC)
      }) Pageable pageable
  ) {
    try {
      Page<PocoDTO> page = pocoService.getPocosPaginado(pageable);
      return ResponseEntity.ok(page);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Erro ao listar poços: " + e.getMessage());
    }
  }

  // POST - criar
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> add(@Valid @RequestBody Poco poco) {
    try {
      Poco novo = pocoService.addPoco(poco);
      return ResponseEntity
          .created(URI.create(String.format("/api/pocos/%s", novo.getCodigoAnp())))
          .body(novo);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Erro ao adicionar poço: " + e.getMessage());
    }
  }

  // PATCH - atualizar parcialmente
  @PatchMapping(value = "/{codigoAnp}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> edit(@PathVariable String codigoAnp,
                                @RequestBody Poco dadosAtualizados) {
    try {
      Poco atualizado = pocoService.editarPoco(codigoAnp, dadosAtualizados);
      return ResponseEntity.ok(atualizado);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Erro ao editar poço: " + e.getMessage());
    }
  }

  // DELETE
  @DeleteMapping("/{codigoAnp}")
  public ResponseEntity<?> delete(@PathVariable String codigoAnp) {
    try {
      boolean removed = pocoService.deletePoco(codigoAnp);
      if (!removed) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Poço não encontrado para exclusão.");
      }
      return ResponseEntity.ok("Poço deletado com sucesso!");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Erro ao deletar poço: " + e.getMessage());
    }
  }
}
