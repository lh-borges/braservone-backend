// com/braservone/services/PocoService.java
package com.braservone.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.braservone.DTO.PocoDTO;
import com.braservone.models.Poco;
import com.braservone.repository.PocoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PocoService {

  private final PocoRepository pocoRepository;

  // ========= QUERIES =========

  @Transactional(readOnly = true)
  public List<Poco> getPocos() {
    return pocoRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Page<PocoDTO> getPocosPaginado(Pageable pageable) {
    return pocoRepository.findAll(pageable).map(PocoDTO::fromEntity);
  }

  @Transactional(readOnly = true)
  public List<PocoDTO> getPocosDTO() {
    return getPocos().stream().map(PocoDTO::fromEntity).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Poco getPocoByCodigoAnp(String codigoAnp) {
    validarCodigoAnp(codigoAnp);
    return findOrThrow(codigoAnp);
  }

  // ========= COMANDOS =========

  @Transactional
  public Poco addPoco(Poco poco) {
    if (poco == null) {
      throw new IllegalArgumentException("Poço não pode ser nulo.");
    }
    validarCodigoAnp(poco.getCodigoAnp());

    // unicidade
    pocoRepository.findByCodigoAnp(poco.getCodigoAnp())
      .ifPresent(p -> { throw new IllegalArgumentException("Já existe poço com código ANP: " + p.getCodigoAnp()); });

    return pocoRepository.save(poco);
  }

  @Transactional
  public Poco editarPoco(String codigoAnp, Poco dados) {
    validarCodigoAnp(codigoAnp);
    if (dados == null) {
      throw new IllegalArgumentException("Dados do poço não podem ser nulos.");
    }
    Poco existente = findOrThrow(codigoAnp);
    existente.atualizar(dados); // mantém sua regra de domínio
    return pocoRepository.save(existente);
  }

  @Transactional
  public void deletePoco(String codigoAnp) {
    validarCodigoAnp(codigoAnp);
    long count = pocoRepository.deleteByCodigoAnp(codigoAnp);
    if (count == 0) {
      throw new EntityNotFoundException("Poço não encontrado para codigoAnp: " + codigoAnp);
    }
  }

  // ========= HELPERS (ficam no service; aqui pode usar repo) =========

  private void validarCodigoAnp(String codigoAnp) {
    if (!StringUtils.hasText(codigoAnp)) {
      throw new IllegalArgumentException("codigoAnp é obrigatório.");
    }
  }

  private Poco findOrThrow(String codigoAnp) {
    return pocoRepository.findByCodigoAnp(codigoAnp)
      .orElseThrow(() -> new EntityNotFoundException("Poço não encontrado para codigoAnp: " + codigoAnp));
  }
}
