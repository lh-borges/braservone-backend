package com.braservone.service;

import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.braservone.models.Operadora;
import com.braservone.repository.EmpresaRepository;
import com.braservone.repository.OperadoraRepository;
import com.braservone.security.services.UserDetailsImpl;

import jakarta.transaction.Transactional;

@Service
public class OperadoraService {

    private final EmpresaRepository empRepository;
    private final OperadoraRepository opRepository;

    public OperadoraService(EmpresaRepository empRepository, OperadoraRepository opRepository) {
        this.empRepository = empRepository;
        this.opRepository = opRepository;
    }

    // =========================
    // MANTIDO (apenas melhorado)
    // =========================
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Operadora> getOperadoras() {
 
        return opRepository.findAll();
    }

    // =========================
    // NOVOS MÉTODOS ÚTEIS
    // =========================

    @Transactional(Transactional.TxType.SUPPORTS)
    public Operadora getById(Long id, Authentication authentication) {
        Long empresaId = getEmpresaId(authentication);
        return getOwnedOrThrow(id);
    }

    @Transactional
    public Operadora create(Operadora payload) {

        // ativo default true se vier nulo
        if (payload.getAtivo() == null) payload.setAtivo(Boolean.TRUE);

        return opRepository.save(payload);
    }

    @Transactional
    public Operadora update(Long id, Operadora payload, Authentication authentication) {
        Long empresaId = getEmpresaId(authentication);
        Operadora current = getOwnedOrThrow(id);

        // atualização campo a campo (sem permitir troca de empresa)
        if (payload.getNome() != null) current.setNome(payload.getNome());
        if (payload.getSiglas() != null) {
            opRepository.findBySiglasIgnoreCase( payload.getSiglas())
                    .filter(op -> !op.getId().equals(id))
                    .ifPresent(op -> { throw new DataIntegrityViolationException("Sigla já cadastrada para esta empresa"); });
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

    @Transactional
    public Operadora toggleAtivo(Long id, boolean value) {
        Operadora current = getOwnedOrThrow(id);
        current.setAtivo(value);
        return opRepository.save(current);
    }

    @Transactional
    public void delete(Long id) {
        Operadora current = getOwnedOrThrow(id);
        opRepository.delete(current);
    }

    // =========================
    // HELPERS
    // =========================
    private Long getEmpresaId(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (userDetails.getEmpresa() == null || userDetails.getEmpresa().getId() == null) {
            throw new IllegalStateException("Usuário sem empresa associada.");
        }
        return userDetails.getEmpresa().getId();
    }

    private Operadora getOwnedOrThrow(Long operadoraId) {
        Operadora op = opRepository.findById(operadoraId)
                .orElseThrow(() -> new IllegalArgumentException("Operadora não encontrada: " + operadoraId));
        
        return op;
    }
}
