// src/main/java/com/braservone/services/VeiculoService.java
package com.braservone.services;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braservone.enums.StatusVeiculos;
import com.braservone.models.Veiculo;
import com.braservone.repository.VeiculoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository repo;

    @Transactional(readOnly = true)
    public List<Veiculo> listar() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Veiculo buscarPorPlaca(String placa) {
        String id = requirePlaca(placa);
        return repo.findById(id)
                   .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado: " + id));
    }


    @Transactional
    public Veiculo criar(Veiculo v) {
        if (v == null) throw new IllegalArgumentException("Veículo não pode ser nulo.");

        String id = requirePlaca(v.getPlaca());
        if (repo.existsById(id)) {
            throw new IllegalArgumentException("Placa já cadastrada: " + id);
        }
        if (v.getStatus() == null) {
            v.setStatus(StatusVeiculos.ATIVO);
        }
        v.setPlaca(id);
        return repo.save(v);
    }



    @Transactional
    public Veiculo atualizar(String placaPath, Veiculo dados) {
        if (dados == null) throw new IllegalArgumentException("Dados do veículo não podem ser nulos.");
        String id = requirePlaca(placaPath);

        Veiculo atual = buscarPorPlaca(id); // 404 se não existir

        if (dados.getApelido() != null && !dados.getApelido().isBlank()) {
            atual.setApelido(dados.getApelido().trim());
        }
        if (dados.getStatus() != null)       atual.setStatus(dados.getStatus());
        if (dados.getTipoVeiculo() != null)  atual.setTipoVeiculo(dados.getTipoVeiculo());
        if (dados.getAnoVeiculo() != null)   atual.setAnoVeiculo(dados.getAnoVeiculo());

        return repo.save(atual);
    }


    @Transactional
    public void alterarStatus(String placa, StatusVeiculos novoStatus) {
        if (novoStatus == null) throw new IllegalArgumentException("novoStatus é obrigatório.");
        Veiculo v = buscarPorPlaca(placa);
        v.setStatus(novoStatus);
        repo.save(v);
    }


    @Transactional
    public void excluir(String placa) {
        Veiculo v = buscarPorPlaca(placa);
        try {
            repo.delete(v);
            repo.flush(); 
        } catch (DataIntegrityViolationException e) {
            // traduz para erro de negócio legível (se preferir 409, trate no @RestControllerAdvice)
            throw new IllegalArgumentException("Veículo possui vínculos e não pode ser excluído.");
        }
    }


    @Transactional(readOnly = true)
    public boolean existePorPlaca(String placa) {
        return repo.existsById(requirePlaca(placa));
    }


    private static String requirePlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("placa é obrigatória.");
        }
        return placa.trim().toUpperCase();
    }
}
