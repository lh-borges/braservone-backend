// src/main/java/com/braservone/controllers/PocoController.java
package com.braservone.controllers;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.braservone.DTO.PocoDTO;
import com.braservone.models.Poco;
import com.braservone.services.PocoService;

@RestController
@RequestMapping("/api/pocos")
public class PocoController {

  private final PocoService pocoService;

  public PocoController(PocoService pocoService) {
    this.pocoService = pocoService;
  }

  @GetMapping(value = "/{codigoAnp}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PocoDTO> getByCodigoAnp(@PathVariable String codigoAnp) {
    var poco = pocoService.getPocoByCodigoAnp(codigoAnp);
    return ResponseEntity.ok(PocoDTO.fromEntity(poco));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<PocoDTO>> listPaged(
      @PageableDefault(size = 50)
      @SortDefault.SortDefaults({
          @SortDefault(sort = "codigoAnp", direction = Sort.Direction.ASC)
      }) Pageable pageable
  ) {
    Page<PocoDTO> page = pocoService.getPocosPaginado(pageable);
    return ResponseEntity.ok(page);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PocoDTO> add(@Valid @RequestBody Poco poco) {
    Poco novo = pocoService.addPoco(poco);
    URI location = URI.create("/api/pocos/" + novo.getCodigoAnp());
    return ResponseEntity.created(location).body(PocoDTO.fromEntity(novo));
  }

  @PatchMapping(value = "/{codigoAnp}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PocoDTO> edit(@PathVariable String codigoAnp,
                                      @Valid @RequestBody Poco dadosAtualizados) {
    Poco atualizado = pocoService.editarPoco(codigoAnp, dadosAtualizados);
    return ResponseEntity.ok(PocoDTO.fromEntity(atualizado));
  }

  @DeleteMapping("/{codigoAnp}")
  public ResponseEntity<Void> delete(@PathVariable String codigoAnp) {
    pocoService.deletePoco(codigoAnp);
    return ResponseEntity.noContent().build();
  }
}
