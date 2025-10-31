package com.braservone.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.braservone.models.Operadora;
import com.braservone.repository.OperadoraRepository;

import jakarta.transaction.Transactional;

@Service
public class OperadoraService {

    private final OperadoraRepository opRepository;

    public OperadoraService(OperadoraRepository opRepository) {
        this.opRepository = opRepository;
    }

    // LISTAR TODAS
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Operadora> getOperadoras() {
        return opRepository.findAll();
    }

    // BUSCAR POR ID
    @Transactional(Transactional.TxType.SUPPORTS)
    public Operadora getById(Long id) {
        return opRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Operadora não encontrada: " + id));
    }

    // CRIAR
    @Transactional
    public Operadora create(Operadora payload) {
        // se não vier ativo, assume true
        if (payload.getAtivo() == null) {
            payload.setAtivo(Boolean.TRUE);
        }

        // se quiser garantir sigla única no sistema inteiro:
        if (payload.getSiglas() != null) {
            opRepository.findBySiglasIgnoreCase(payload.getSiglas())
                    .ifPresent(op -> {
                        throw new DataIntegrityViolationException("Sigla já cadastrada");
                    });
        }

        return opRepository.save(payload);
    }

    // ATUALIZAR
    @Transactional
    public Operadora update(Long id, Operadora payload) {
        Operadora current = getById(id);

        if (payload.getNome() != null) current.setNome(payload.getNome());

        if (payload.getSiglas() != null) {
            opRepository.findBySiglasIgnoreCase(payload.getSiglas())
                    .filter(op -> !op.getId().equals(id))
                    .ifPresent(op -> {
                        throw new DataIntegrityViolationException("Sigla já cadastrada");
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
        opRepository.delete(current);
    }
}
