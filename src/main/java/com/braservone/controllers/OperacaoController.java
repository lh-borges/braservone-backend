// com/projetopetroleo/controllers/OperacaoController.java
package com.braservone.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.braservone.DTO.OperacaoCreateDTO;
import com.braservone.DTO.OperacaoResponseDTO;
import com.braservone.DTO.OperacaoUpdateDTO;
import com.braservone.services.OperacaoService;
import com.google.gson.Gson;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/operacoes")
@CrossOrigin(origins = "*") // ajuste conforme seu front
public class OperacaoController {

  private final OperacaoService service;

  public OperacaoController(OperacaoService service) {
    this.service = service;
  }

  // CREATE
  @PostMapping
  public OperacaoResponseDTO create(@RequestBody @Valid OperacaoCreateDTO dto) {
    return service.create(dto);
  }

  // READ (by id)
  @GetMapping("/{id}")
  public OperacaoResponseDTO get(@PathVariable Long id) {
    return service.getById(id);
  }

  // LIST (com filtros)
  @GetMapping
  public Page<OperacaoResponseDTO> list(
      @RequestParam(required = false) Boolean status,
      @RequestParam(required = false) Long operadoraId,
      @RequestParam(required = false) String pocoCodigoAnp,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id,desc") String sort) {

    Sort s;
    if (sort.contains(",")) {
      String[] p = sort.split(",", 2);
      s = Sort.by(Sort.Direction.fromString(p[1]), p[0]);
    } else {
      s = Sort.by(Sort.Direction.DESC, sort);
    }
    Pageable pageable = PageRequest.of(page, size, s);
    return service.list(status, operadoraId, pocoCodigoAnp, pageable);
  }

  // UPDATE (PATCH)
  @PatchMapping("/{id}")
  public OperacaoResponseDTO update(@PathVariable Long id, @RequestBody @Valid OperacaoUpdateDTO dto) {
    return service.update(id, dto);
  }

  // DELETE
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
