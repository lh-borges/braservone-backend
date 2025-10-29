package com.braservone.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braservone.DTO.EmpresaDTO;
import com.braservone.models.Empresa;
import com.braservone.repository.EmpresaRepository;

@Service
public class EmpresaService {

  private final EmpresaRepository empresaRepository;

  public EmpresaService(EmpresaRepository empresaRepository) {
    this.empresaRepository = empresaRepository;
  }

  // ========================
  // Converters
  // ========================
  public EmpresaDTO toDTO(Empresa emp) {
    if (emp == null) return null;
    EmpresaDTO dto = new EmpresaDTO();
    dto.setId(emp.getId());
    dto.setNome(emp.getNome());
    dto.setCpnj(emp.getCnpj());
    return dto;
  }


  @Transactional(readOnly = true)
  public Optional<Empresa> findById(Long id) {
    return empresaRepository.findById(id);
  }

  // ========================
  // Commands
  // ========================
  @Transactional
  public Empresa addEmpresa(Empresa empresa) {
    // Validações simples (ajuste conforme suas regras)
    if (empresa.getNome() == null || empresa.getNome().isBlank()) {
      throw new IllegalArgumentException("Nome da empresa é obrigatório");
    }
    if (empresa.getCnpj() == null || empresa.getCnpj().isBlank()) {
      throw new IllegalArgumentException("CNPJ da empresa é obrigatório");
    }
    return empresaRepository.save(empresa);
  }

  // ========================
  // Defaults (se você precisar de uma empresa padrão)
  // ========================
  @Transactional(readOnly = true)
  public Optional<Empresa> getDefaultEmpresa() {
    // Implemente no repository um seletor adequado à sua regra de negócio:
    // 1) Por flag (ex.: is_default = true)
    // 2) Ou por nome fixo
    // 3) Ou a primeira empresa cadastrada
    //
    // Exemplos:
    // return empresaRepository.findFirstByIsDefaultTrue();
    // return empresaRepository.findByNome("Empresa Padrão");
    // return empresaRepository.findFirstByOrderByIdAsc();
    return Optional.empty(); // placeholder até você definir a regra
  }
}
