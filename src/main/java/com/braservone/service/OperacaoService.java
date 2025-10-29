// com/projetopetroleo/service/OperacaoService.java
package com.braservone.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braservone.DTO.OperacaoCreateDTO;
import com.braservone.DTO.OperacaoResponseDTO;
import com.braservone.DTO.OperacaoUpdateDTO;
import com.braservone.mapper.OperacaoMapper;
import com.braservone.models.Operacao;
import com.braservone.models.Operadora;
import com.braservone.models.Poco;
import com.braservone.repository.OperacaoRepository;
import com.braservone.repository.OperadoraRepository;
import com.braservone.repository.PocoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class OperacaoService {

  private final OperacaoRepository operacaoRepo;
  private final PocoRepository pocoRepo;
  private final OperadoraRepository operadoraRepo;

  public OperacaoService(OperacaoRepository operacaoRepo,
                         PocoRepository pocoRepo,
                         OperadoraRepository operadoraRepo) {
    this.operacaoRepo = operacaoRepo;
    this.pocoRepo = pocoRepo;
    this.operadoraRepo = operadoraRepo;
  }

  // CREATE
  @Transactional
  public OperacaoResponseDTO create(OperacaoCreateDTO dto) {
    Poco poco = pocoRepo.findByCodigoAnp(dto.pocoCodigoAnp())
        .orElseThrow(() -> new EntityNotFoundException("Poço não encontrado: " + dto.pocoCodigoAnp()));

    Operadora op = operadoraRepo.findById(dto.operadoraId())
        .orElseThrow(() -> new EntityNotFoundException("Operadora não encontrada: id=" + dto.operadoraId()));

    if (dto.dataInicio() != null && dto.dataFinal() != null &&
        dto.dataFinal().isBefore(dto.dataInicio())) {
      throw new IllegalArgumentException("dataFinal não pode ser anterior a dataInicio.");
    }

    Operacao entity = new Operacao();
    
    
    entity.setPoco(poco);
    entity.setOperadora(op);
    entity.setNomeOperacao(Objects.requireNonNull(dto.nomeOperacao()));
    entity.setStatus(dto.status());
    entity.setDataInicio(dto.dataInicio());
    entity.setDataFinal(dto.dataFinal());

    return OperacaoMapper.toDTO(operacaoRepo.save(entity));
  }

  // READ
  @Transactional(readOnly = true)
  public OperacaoResponseDTO getById(Long id) {
    Operacao o = operacaoRepo.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Operação não encontrada: id=" + id));
    return OperacaoMapper.toDTO(o);
  }

  @Transactional(readOnly = true)
  public Page<OperacaoResponseDTO> list(Boolean status, Long operadoraId, String pocoCodigoAnp, Pageable pageable) {
    return operacaoRepo.search(status, operadoraId, pocoCodigoAnp, pageable).map(OperacaoMapper::toDTO);
  }

  // UPDATE (PATCH)
  @Transactional
  public OperacaoResponseDTO update(Long id, OperacaoUpdateDTO dto) {
    Operacao entity = operacaoRepo.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Operação não encontrada: id=" + id));

    if (dto.nomeOperacao() != null && !dto.nomeOperacao().isBlank()) {
      entity.setNomeOperacao(dto.nomeOperacao());
    }

    if (dto.pocoCodigoAnp() != null) {
      Poco poco = pocoRepo.findByCodigoAnp(dto.pocoCodigoAnp())
          .orElseThrow(() -> new EntityNotFoundException("Poço não encontrado: " + dto.pocoCodigoAnp()));
      entity.setPoco(poco);
    }

    if (dto.operadoraId() != null) {
      Operadora op = operadoraRepo.findById(dto.operadoraId())
          .orElseThrow(() -> new EntityNotFoundException("Operadora não encontrada: id=" + dto.operadoraId()));
      entity.setOperadora(op);
    }

    if (dto.status() != null) entity.setStatus(dto.status());
    if (dto.dataInicio() != null) entity.setDataInicio(dto.dataInicio());
    if (dto.dataFinal()  != null) entity.setDataFinal(dto.dataFinal());

    if (entity.getDataInicio() != null && entity.getDataFinal() != null &&
        entity.getDataFinal().isBefore(entity.getDataInicio())) {
      throw new IllegalArgumentException("dataFinal não pode ser anterior a dataInicio.");
    }

    return OperacaoMapper.toDTO(operacaoRepo.save(entity));
  }

  // DELETE
  @Transactional
  public void delete(Long id) {
    if (!operacaoRepo.existsById(id)) {
      throw new EntityNotFoundException("Operação não encontrada: id=" + id);
    }
    operacaoRepo.deleteById(id);
  }
}
