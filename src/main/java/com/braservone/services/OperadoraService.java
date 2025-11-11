// src/main/java/com/braservone/services/OperadoraService.java
package com.braservone.services;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.braservone.models.Operadora;
import com.braservone.repository.OperadoraRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperadoraService {

    private final OperadoraRepository opRepository;

    // LISTAR TODAS
    @Transactional(readOnly = true)
    public List<Operadora> getOperadoras() {
        return opRepository.findAll();
    }

    // BUSCAR POR ID
    @Transactional(readOnly = true)
    public Operadora getById(Long id) {
        return opRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Operadora não encontrada: " + id));
    }

    // CRIAR
    @Transactional
    public Operadora create(Operadora payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Operadora não pode ser nula.");
        }

        // default: ativo = true
        if (payload.getAtivo() == null) {
            payload.setAtivo(Boolean.TRUE);
        }

        // unicidade de sigla (camada de negócio)
        if (StringUtils.hasText(payload.getSiglas())) {
            opRepository.findBySiglasIgnoreCase(payload.getSiglas())
                    .ifPresent(op -> {
                        throw new IllegalArgumentException("Sigla já cadastrada");
                    });
        }

        return opRepository.save(payload);
    }

    // ATUALIZAR (parcial)
    @Transactional
    public Operadora update(Long id, Operadora payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Dados da operadora não podem ser nulos.");
        }

        Operadora current = getById(id);

        if (StringUtils.hasText(payload.getNome())) current.setNome(payload.getNome());

        if (StringUtils.hasText(payload.getSiglas())) {
            opRepository.findBySiglasIgnoreCase(payload.getSiglas())
                    .filter(op -> !op.getId().equals(id))
                    .ifPresent(op -> {
                        throw new IllegalArgumentException("Sigla já cadastrada");
                    });
            current.setSiglas(payload.getSiglas());
        }

        if (payload.getPais() != null) current.setPais(payload.getPais());
        if (payload.getEndereco() != null) current.setEndereco(payload.getEndereco());
        if (payload.getEmailContato() != null) current.setEmailContato(payload.getEmailContato());
        if (payload.getTelefoneContato() != null) current.setTelefoneContato(payload.getTelefoneContato());
        if (payload.getResponsaveltecnico() != null) current.setResponsaveltecnico(payload.getResponsaveltecnico());
        if (payload.getAtivo() != null) current.setAtivo(payload.getAtivo());

        return opRepository.save(current);
    }

    // ATIVAR/DESATIVAR
    @Transactional
    public Operadora toggleAtivo(Long id, boolean value) {
        Operadora current = getById(id);
        current.setAtivo(value);
        return opRepository.save(current);
    }

    // DELETAR
    @Transactional
    public void delete(Long id) {
        Operadora current = getById(id);
        try {
            opRepository.delete(current);
            opRepository.flush(); // força checagem de FK/unique agora
        } catch (DataIntegrityViolationException e) {
            // Converte 500 técnico em 400 de regra de negócio (ou 409 se preferir)
            throw new IllegalArgumentException("Operadora possui vínculos e não pode ser excluída.");
        }
    }
}
