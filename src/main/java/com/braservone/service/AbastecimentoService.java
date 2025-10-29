// com.projetopetroleo.service.AbastecimentoService
package com.braservone.service;

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

    // ✅ injeção por construtor (mais segura que @Autowired em field)
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

        // campos do modelo atual
        a.setDistRodadaKm(dto.getDistRodadaKm());
        a.setVolumeLitros(dto.getVolumeLitros());
        a.setValorTotal(dto.getValorTotal());
        a.setValorPorLitro(dto.getValorPorLitro());
        a.setMediaKmPorL(dto.getMediaKmPorL());
        a.setMediaRsPorKm(dto.getMediaRsPorKm());
        a.setDataAbastecimento(dto.getDataAbastecimento());

        validarRegrasMinimas(a);

        a = repo.save(a);
        return toResponse(a);
    }

    // ========= UPDATE =========
    @Transactional
    public AbastecimentoResponseDTO atualizar(Long id, @Valid AbastecimentoCreateDTO dto) {
        Abastecimento a = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Abastecimento não encontrado: " + id));

        // pode trocar o veículo (placa) na edição
        Veiculo v = buscarVeiculoOu404(dto.getPlacaVeiculo());
        a.setVeiculo(v);

        a.setDistRodadaKm(dto.getDistRodadaKm());
        a.setVolumeLitros(dto.getVolumeLitros());
        a.setValorTotal(dto.getValorTotal());
        a.setValorPorLitro(dto.getValorPorLitro());
        a.setMediaKmPorL(dto.getMediaKmPorL());
        a.setMediaRsPorKm(dto.getMediaRsPorKm());
        a.setDataAbastecimento(dto.getDataAbastecimento());

        validarRegrasMinimas(a);

        a = repo.save(a);
        return toResponse(a);
    }

    // ========= READ =========

    /** Lista geral paginada (para abrir a tela já com dados). */
    @Transactional(readOnly = true)
    public Page<AbastecimentoResponseDTO> listarTodos(Pageable pageable) {
        return repo.findAll(pageable).map(this::toResponse);
    }

    /** Lista paginada por placa (filtro). */
    @Transactional(readOnly = true)
    public Page<AbastecimentoResponseDTO> listarPorVeiculo(String placa, Pageable pageable) {
        String p = normaliza(placa);
        // Se existir no repository: Page<Abastecimento> findByVeiculo_PlacaOrderByDataAbastecimentoDesc(String, Pageable)
        return repo.findByVeiculo_PlacaOrderByDataAbastecimentoDesc(p, pageable)
                   .map(this::toResponse);
    }

    /** Busca um registro pelo id (útil no fluxo de edição direta). */
    @Transactional(readOnly = true)
    public AbastecimentoResponseDTO buscarUm(Long id) {
        Abastecimento a = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Abastecimento não encontrado: " + id));
        return toResponse(a);
    }

    // ========= DELETE =========
    @Transactional
    public void excluir(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Abastecimento não encontrado: " + id);
        }
        repo.deleteById(id);
    }

    // ========= Helpers =========

    private void validarRegrasMinimas(Abastecimento a) {
        if (a.getVolumeLitros() == null) {
            throw new IllegalArgumentException("Volume de abastecimento é obrigatório.");
        }
        if (a.getValorTotal() == null && a.getValorPorLitro() == null) {
            throw new IllegalArgumentException("Informe 'valorTotal' ou 'valorPorLitro'.");
        }
    }

    private Veiculo buscarVeiculoOu404(String placaRaw) {
        String placa = normaliza(placaRaw);
        return veiculoRepo.findById(placa)
                .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado: " + placaRaw));
    }

    private AbastecimentoResponseDTO toResponse(Abastecimento a) {
        AbastecimentoResponseDTO r = new AbastecimentoResponseDTO();
        r.setId(a.getId());
        r.setPlacaVeiculo(a.getVeiculo().getPlaca());
        r.setDistRodadaKm(a.getDistRodadaKm());
        r.setVolumeLitros(a.getVolumeLitros());
        r.setValorTotal(a.getValorTotal());
        r.setValorPorLitro(a.getValorPorLitro());
        r.setMediaKmPorL(a.getMediaKmPorL());
        r.setMediaRsPorKm(a.getMediaRsPorKm());
        r.setDataAbastecimento(a.getDataAbastecimento());
        return r;
    }

    private String normaliza(String placa) {
        return placa == null ? null : placa.trim().toUpperCase();
    }
}
