package com.braservone.services;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braservone.enums.Setor;
import com.braservone.enums.StatusReporte;
import com.braservone.models.Reporte;
import com.braservone.models.Veiculo;
import com.braservone.repository.ReporteRepository;
import com.braservone.repository.VeiculoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReporteService {

    private final ReporteRepository repository;
    private final VeiculoRepository veiculoRepository; // << novo

    // grupos de status
    private static final Set<StatusReporte> ABERTOS = EnumSet.of(
        StatusReporte.NOVO,
        StatusReporte.EM_ANDAMENTO,
        StatusReporte.NO_FINANCEIRO,
        StatusReporte.NO_COMPRAS,
        StatusReporte.ESPERANDO_ENVIO,
        StatusReporte.NA_MANUTENCAO
    );

    private static final Set<StatusReporte> ENCERRADOS = EnumSet.of(
        StatusReporte.FINALIZADO,
        StatusReporte.CANCELADO
    );

    private static boolean podeTransicionar(StatusReporte atual, StatusReporte novo) {
        if (novo == null) return false;
        if (atual == null) return true;
        if (atual == novo) return true;
        if (ENCERRADOS.contains(atual)) return false;
        return ABERTOS.contains(novo) || ENCERRADOS.contains(novo);
    }

    /** Cria reporte com veículo opcional (por placa). */
    public Reporte create(Reporte r, String veiculoPlaca) {
        r.setId(null);
        if (r.getDataHoraReporte() == null) r.setDataHoraReporte(LocalDateTime.now());
        if (r.getStatus() == null) r.setStatus(StatusReporte.NOVO);

        if (veiculoPlaca != null && !veiculoPlaca.isBlank()) {
            Veiculo v = veiculoRepository.findById(veiculoPlaca)
                    .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado: placa=" + veiculoPlaca));
            r.setVeiculo(v);
        } else {
            r.setVeiculo(null);
        }
        return repository.save(r);
    }

    @Transactional(readOnly = true)
    public Reporte get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reporte não encontrado: id=" + id));
    }

    /**
     * Lista com filtros opcionais; se placa vier preenchida, filtra em memória.
     * (Se preferir, leve esse filtro para o repository/consultas JPQL.)
     */
    @Transactional(readOnly = true)
    public List<Reporte> list(StatusReporte status, Setor setor,
                              LocalDateTime from, LocalDateTime to, String veiculoPlaca) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("Intervalo de datas inválido: 'from' não pode ser maior que 'to'.");
        }
        List<Reporte> base = repository.search(status, setor, from, to);
        if (veiculoPlaca == null || veiculoPlaca.isBlank()) return base;

        // filtra por placa (pode acionar LAZY ao acessar r.getVeiculo())
        return base.stream()
                .filter(r -> r.getVeiculo() != null
                        && veiculoPlaca.equalsIgnoreCase(r.getVeiculo().getPlaca()))
                .toList();
    }

    public Reporte updateStatus(Long id, StatusReporte novoStatus) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("O novo status é obrigatório.");
        }
        Reporte r = get(id);
        StatusReporte atual = r.getStatus();
        if (!podeTransicionar(atual, novoStatus)) {
            throw new IllegalArgumentException("Transição de status inválida: " + atual + " -> " + novoStatus);
        }
        r.setStatus(novoStatus);
        return repository.save(r);
    }

    /** Associa ou desassocia um veículo ao reporte (passando placa; null/blank desassocia). */
    public Reporte updateVeiculo(Long id, String veiculoPlaca) {
        Reporte r = get(id);
        if (veiculoPlaca == null || veiculoPlaca.isBlank()) {
            r.setVeiculo(null); // desassocia
        } else {
            Veiculo v = veiculoRepository.findById(veiculoPlaca)
                    .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado: placa=" + veiculoPlaca));
            r.setVeiculo(v);
        }
        return repository.save(r);
    }

    public void delete(Long id) {
        Reporte r = get(id);
        repository.delete(r);
    }
}
