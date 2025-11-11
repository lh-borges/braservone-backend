// src/main/java/com/braservone/services/AbastecimentoService.java
package com.braservone.services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braservone.DTO.AbastecimentoCreateDTO;
import com.braservone.DTO.AbastecimentoResponseDTO;
import com.braservone.models.Abastecimento;
import com.braservone.models.Veiculo;
import com.braservone.repository.AbastecimentoRepository;
import com.braservone.repository.VeiculoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Service
public class AbastecimentoService {

    private final AbastecimentoRepository repo;
    private final VeiculoRepository veiculoRepo;

    public AbastecimentoService(AbastecimentoRepository repo, VeiculoRepository veiculoRepo) {
        this.repo = repo;
        this.veiculoRepo = veiculoRepo;
    }

    // ========= CREATE =========
    @Transactional
    public AbastecimentoResponseDTO criar(@Valid AbastecimentoCreateDTO dto) {
        Veiculo v = buscarVeiculoOu404(dto.getPlacaVeiculo());

        Abastecimento a = new Abastecimento();
        a.setVeiculo(v);
        a.setDistRodadaKm(dto.getDistRodadaKm());
        a.setVolumeLitros(dto.getVolumeLitros());
        a.setValorTotal(dto.getValorTotal());
        a.setValorPorLitro(dto.getValorPorLitro());
        a.setMediaKmPorL(dto.getMediaKmPorL());
        a.setMediaRsPorKm(dto.getMediaRsPorKm());
        a.setDataAbastecimento(dto.getDataAbastecimento());

        preencherDerivados(a);

        a = repo.save(a);
        return AbastecimentoResponseDTO.toResponse(a);
    }

    // ========= UPDATE (PUT/PATCH) =========
    @Transactional
    public AbastecimentoResponseDTO atualizar(Long id, @Valid AbastecimentoCreateDTO dto) {
        Abastecimento a = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Abastecimento não encontrado: " + id));

        Veiculo v = buscarVeiculoOu404(dto.getPlacaVeiculo());
        a.setVeiculo(v);

        if (dto.getDistRodadaKm() != null) a.setDistRodadaKm(dto.getDistRodadaKm());
        if (dto.getVolumeLitros() != null) a.setVolumeLitros(dto.getVolumeLitros());
        if (dto.getValorTotal() != null) a.setValorTotal(dto.getValorTotal());
        if (dto.getValorPorLitro() != null) a.setValorPorLitro(dto.getValorPorLitro());
        if (dto.getMediaKmPorL() != null) a.setMediaKmPorL(dto.getMediaKmPorL());
        if (dto.getMediaRsPorKm() != null) a.setMediaRsPorKm(dto.getMediaRsPorKm());
        if (dto.getDataAbastecimento() != null) a.setDataAbastecimento(dto.getDataAbastecimento());

        preencherDerivados(a);

        a = repo.save(a);
        return AbastecimentoResponseDTO.toResponse(a);
    }

    // ========= READ =========
    @Transactional(readOnly = true)
    public Page<AbastecimentoResponseDTO> listarTodos(Pageable pageable) {
        return repo.findAll(pageable).map(AbastecimentoResponseDTO::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AbastecimentoResponseDTO> listarPorVeiculo(String placa, Pageable pageable) {
        String p = normaliza(placa);
        return repo.findByVeiculo_PlacaOrderByDataAbastecimentoDesc(p, pageable)
                   .map(AbastecimentoResponseDTO::toResponse);
    }

    @Transactional(readOnly = true)
    public AbastecimentoResponseDTO buscarUm(Long id) {
        Abastecimento a = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Abastecimento não encontrado: " + id));
        return AbastecimentoResponseDTO.toResponse(a);
    }

    // ========= DELETE =========
    @Transactional
    public void excluir(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Abastecimento não encontrado: " + id);
        }
        try {
            repo.deleteById(id);
            repo.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Registro de abastecimento possui vínculos e não pode ser excluído.");
        }
    }


    private Veiculo buscarVeiculoOu404(String placaRaw) {
        String placa = normaliza(placaRaw);
        return veiculoRepo.findById(placa)
                .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado: " + placaRaw));
    }

    private String normaliza(String placa) {
        return placa == null ? null : placa.trim().toUpperCase();
    }

    private void preencherDerivados(Abastecimento a) {
        BigDecimal vol = a.getVolumeLitros();
        BigDecimal vpl = a.getValorPorLitro();
        BigDecimal vt  = a.getValorTotal();

        if (vol != null && vpl != null && vt == null) {
            a.setValorTotal(vpl.multiply(vol).setScale(2, RoundingMode.HALF_UP));
        } else if (vol != null && vt != null && vpl == null && vol.signum() != 0) {
            a.setValorPorLitro(vt.divide(vol, 3, RoundingMode.HALF_UP));
        }
    }
}
