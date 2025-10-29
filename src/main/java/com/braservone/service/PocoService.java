// src/main/java/com/projetopetroleo/service/PocoService.java
package com.braservone.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braservone.DTO.PocoDTO;
import com.braservone.models.Poco;
import com.braservone.repository.EmpresaRepository;
import com.braservone.repository.PocoRepository;
import com.braservone.repository.UserRepository;
import com.braservone.security.jwt.JwtUtils;
import com.braservone.security.services.UserDetailsImpl;

@Service
public class PocoService {

  private final PocoRepository pocoRepository;
  private final UserRepository userRepository;
  private final EmpresaRepository empRepository;
  private final JwtUtils jwtUtils;

  public PocoService(
      PocoRepository pocoRepository,
      UserRepository userRepository,
      JwtUtils jwtUtils,
      EmpresaRepository empRepository
  ) {
    this.pocoRepository = pocoRepository;
    this.userRepository = userRepository;
    this.empRepository = empRepository;
    this.jwtUtils = jwtUtils;
  }

  // ========= CONSULTAS =========

  @Transactional(readOnly = true)
  public Poco getPocoByCodigoAnp(String codigoAnp, Authentication auth) {
    Long empresaId = resolveEmpresaIdDoUsuarioAtual();
    return pocoRepository.findByCodigoAnp(codigoAnp)
        .orElseThrow(() -> new IllegalArgumentException(
            "Poço não encontrado para codigoAnp=" + codigoAnp + " na empresa=" + empresaId));
  }

  @Transactional(readOnly = true)
  public List<Poco> getPocosDaEmpresa() {
    Long empresaId = resolveEmpresaIdDoUsuarioAtual();
    return pocoRepository.findAll();
  }

  /** NOVO: paginação em DTO */
  @Transactional(readOnly = true)
  public Page<PocoDTO> getPocosPaginado(Pageable pageable) {
    // Quando houver multi-tenant, aplique o filtro por empresaId aqui
    return pocoRepository.findAll(pageable).map(this::toDTO);
  }

  // ========= COMANDOS =========

  @Transactional
  public Poco addPoco(Poco poco) {
    try {
      pocoRepository.findByCodigoAnp(poco.getCodigoAnp()).ifPresent(p -> {
        throw new IllegalArgumentException("Já existe poço com código ANP: " + p.getCodigoAnp());
      });
      return pocoRepository.save(poco);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao salvar poço: " + e.getMessage(), e);
    }
  }

  @Transactional
  public Poco editarPoco(String codigoAnp, Poco dados, Authentication auth) {
    Poco existente = getPocoByCodigoAnp(codigoAnp, auth);
    existente.atualizar(dados);
    return pocoRepository.save(existente);
  }

  @Transactional
  public boolean deletePoco(String codigoAnp, Authentication auth) {
    UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
    Long empresaId = userDetails.getEmpresa() != null ? userDetails.getEmpresa().getId() : null;
    long count = pocoRepository.deleteByCodigoAnp(codigoAnp);
    return count > 0;
  }

  // ========= VERSÃO EM DTO (lista completa, ainda disponível) =========

  @Transactional(readOnly = true)
  public List<PocoDTO> getPocosDaEmpresaDTO() {
    List<Poco> pocos = getPocosDaEmpresa();
    return pocos.stream().map(this::toDTO).collect(Collectors.toList());
  }

  private PocoDTO toDTO(Poco p) {
    PocoDTO dto = new PocoDTO();
    dto.setCodANP(p.getCodigoAnp());
    dto.setBacia(p.getBacia());
    dto.setStatus(p.getStatus());
    dto.setFluido(p.getFluido() != null ? p.getFluido().name() : null); // null-safe
    dto.setNomeCampo(p.getNomeCampo());
    dto.setLocal(p.getLocal());
    dto.setLatitude(p.getLatitude());
    dto.setLongitude(p.getLongitude());
    return dto;
  }

  // ========= SUPORTE =========

  private Long resolveEmpresaIdDoUsuarioAtual() {
    Authentication auth = jwtUtils.getCurrentAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      throw new AccessDeniedException("Usuário não autenticado.");
    }

    if (auth.getPrincipal() instanceof UserDetailsImpl ud) {
      if (ud.getEmpresa() != null && ud.getEmpresa().getId() != null) {
        return ud.getEmpresa().getId();
      }
    }

    String username = auth.getName();
    return userRepository.findByUsername(username)
        .map(u -> {
          if (u.getEmpresa() == null || u.getEmpresa().getId() == null) {
            throw new IllegalStateException("Usuário sem empresa associada.");
          }
          return u.getEmpresa().getId();
        })
        .orElseThrow(() -> new IllegalStateException("Usuário não encontrado: " + username));
  }

  @Transactional
  public Poco save(Poco poco) {
    return pocoRepository.save(poco);
  }
}
