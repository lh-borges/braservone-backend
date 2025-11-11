// src/main/java/com/braservone/services/OperacaoService.java
package com.braservone.services;

import java.util.Objects;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperacaoService {

  private final OperacaoRepository operacaoRepo;
  private final PocoRepository pocoRepo;
  private final OperadoraRepository operadoraRepo;

  // CREATE
  @Transactional
  public OperacaoResponseDTO create(OperacaoCreateDTO dto) {
    if (dto == null) throw new IllegalArgumentException("Payload da operação não pode ser nulo.");

    Poco poco = pocoRepo.findByCodigoAnp(dto.pocoCodigoAnp())
        .orElseThrow(() -> new EntityNotFoundException("Poço não encontrado: " + dto.pocoCodigoAnp()));

    Operadora op = operadoraRepo.findById(dto.operadoraId())
        .orElseThrow(() -> new EntityNotFoundException("Operadora não encontrada: id=" + dto.operadoraId()));

    String nome = Objects.requireNonNull(dto.nomeOperacao(), "nomeOperacao é obrigatório.");
    if (!StringUtils.hasText(nome)) {
      throw new IllegalArgumentException("nomeOperacao não pode ser vazio.");
    }

    validarDatas(dto.dataInicio(), dto.dataFinal());

    Operacao entity = new Operacao();
    entity.setPoco(poco);
    entity.setOperadora(op);
    entity.setNomeOperacao(nome);
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
    if (dto == null) throw new IllegalArgumentException("Payload da operação não pode ser nulo.");

    Operacao entity = operacaoRepo.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Operação não encontrada: id=" + id));

    if (StringUtils.hasText(dto.nomeOperacao())) {
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

    validarDatas(entity.getDataInicio(), entity.getDataFinal());

    return OperacaoMapper.toDTO(operacaoRepo.save(entity));
  }

  // DELETE
  @Transactional
  public void delete(Long id) {
    if (!operacaoRepo.existsById(id)) {
      throw new EntityNotFoundException("Operação não encontrada: id=" + id);
    }
    try {
      operacaoRepo.deleteById(id);
      operacaoRepo.flush(); // força checagem de FK agora
    } catch (DataIntegrityViolationException e) {
      // Converta para erro de negócio compreensível (seu @RestControllerAdvice devolve 400/409)
      throw new IllegalArgumentException("Operação possui vínculos e não pode ser excluída.");
    }
  }

  // ===== Helpers (coesos ao service) =====
  private static <T extends Comparable<T>> void validarDatas(T inicio, T fim) {
    if (inicio != null && fim != null && fim.compareTo(inicio) < 0) {
      throw new IllegalArgumentException("dataFinal não pode ser anterior a dataInicio.");
    }
  }
}
