// src/main/java/com/projetopetroleo/service/FornecedorService.java
package com.braservone.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braservone.models.Fornecedor;
import com.braservone.repository.FornecedorRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    @Transactional(readOnly = true)
    public List<Fornecedor> listar(String q) {
        if (q == null || q.isBlank()) return fornecedorRepository.findAll();
        return fornecedorRepository.findByNomeContainingIgnoreCase(q.trim());
    }

    @Transactional(readOnly = true)
    public Fornecedor buscar(Long id) {
        return fornecedorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Fornecedor não encontrado: " + id));
    }

    @Transactional
    public Fornecedor criar(@Valid Fornecedor payload) {
        // Deixa o banco gerar o ID (IDENTITY/AUTO_INCREMENT)
        payload.setId(null);
        validarObrigatorios(payload);
        return fornecedorRepository.save(payload);
    }

    @Transactional
    public Fornecedor atualizar(Long id, @Valid Fornecedor patch) {
        Fornecedor entidade = buscar(id);
        if (patch.getNome() != null && !patch.getNome().isBlank()) {
            entidade.setNome(patch.getNome().trim());
        }
        if (patch.getTipo() != null) {
            entidade.setTipo(patch.getTipo());
        }
        return fornecedorRepository.save(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        if (!fornecedorRepository.existsById(id)) {
            throw new EntityNotFoundException("Fornecedor não encontrado: " + id);
        }
        try {
            fornecedorRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // FK em quimico.fornecedor_id
            throw new IllegalStateException(
                "Não é possível excluir: há Químicos referenciando este fornecedor.", e);
        }
    }

    // ---- helpers ----
    private void validarObrigatorios(Fornecedor f) {
        if (f.getNome() == null || f.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (f.getTipo() == null) {
            throw new IllegalArgumentException("Tipo é obrigatório.");
        }
    }
}
