package com.braservone.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braservone.DTO.PocoDTO;
import com.braservone.models.Poco;
import com.braservone.repository.PocoRepository;

@Service
public class PocoService {

    private final PocoRepository pocoRepository;

    public PocoService(PocoRepository pocoRepository) {
        this.pocoRepository = pocoRepository;
    }

    // ========= CONSULTAS =========

    @Transactional(readOnly = true)
    public Poco getPocoByCodigoAnp(String codigoAnp) {
        return pocoRepository.findByCodigoAnp(codigoAnp)
                .orElseThrow(() ->
                        new IllegalArgumentException("Poço não encontrado para codigoAnp=" + codigoAnp));
    }

    @Transactional(readOnly = true)
    public List<Poco> getPocos() {
        return pocoRepository.findAll();
    }

    /** paginação em DTO */
    @Transactional(readOnly = true)
    public Page<PocoDTO> getPocosPaginado(Pageable pageable) {
        return pocoRepository.findAll(pageable).map(this::toDTO);
    }

    // ========= COMANDOS =========

    @Transactional
    public Poco addPoco(Poco poco) {
        // regra de unicidade de código ANP
        pocoRepository.findByCodigoAnp(poco.getCodigoAnp())
                .ifPresent(p -> {
                    throw new IllegalArgumentException(
                            "Já existe poço com código ANP: " + p.getCodigoAnp());
                });

        return pocoRepository.save(poco);
    }

    @Transactional
    public Poco editarPoco(String codigoAnp, Poco dados) {
        Poco existente = getPocoByCodigoAnp(codigoAnp);

        // se tua entidade tiver o método atualizar(...)
        existente.atualizar(dados);

        return pocoRepository.save(existente);
    }

    @Transactional
    public boolean deletePoco(String codigoAnp) {
        long count = pocoRepository.deleteByCodigoAnp(codigoAnp);
        return count > 0;
    }

    // ========= DTOs =========

    @Transactional(readOnly = true)
    public List<PocoDTO> getPocosDTO() {
        return getPocos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ====== AQUI ESTÁ O MÉTODO QUE FALTAVA ======
    private PocoDTO toDTO(Poco p) {
        PocoDTO dto = new PocoDTO();
        dto.setCodANP(p.getCodigoAnp());
        dto.setBacia(p.getBacia());
        dto.setStatus(p.getStatus());
        dto.setFluido(p.getFluido() != null ? p.getFluido().name() : null);
        dto.setNomeCampo(p.getNomeCampo());
        dto.setLocal(p.getLocal());
        dto.setLatitude(p.getLatitude());
        dto.setLongitude(p.getLongitude());
        return dto;
    }
    // ============================================

    @Transactional
    public Poco save(Poco poco) {
        return pocoRepository.save(poco);
    }
}
