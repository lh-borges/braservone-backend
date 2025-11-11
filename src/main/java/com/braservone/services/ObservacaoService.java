package com.braservone.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braservone.models.Observacao;
import com.braservone.models.Reporte;
import com.braservone.repository.ReporteRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ObservacaoService {

    private final ReporteRepository reporteRepository;

    @Transactional(readOnly = true)
    public List<Observacao> listarPorReporte(Long reporteId) {
        Reporte reporte = getReporteOrThrow(reporteId);

        // Força inicialização dentro da transação
        List<Observacao> obs = reporte.getListObservacoes();
        obs.size(); // toca na coleção => Hibernate inicializa

        // Retorna cópia desacoplada da Persistence Context
        return new java.util.ArrayList<>(obs);
    }


    // Adiciona uma nova observação a um reporte
    public Observacao adicionar(Long reporteId, Observacao nova) {
        if (nova == null || nova.getMensagem() == null || nova.getMensagem().isBlank()) {
            throw new IllegalArgumentException("Mensagem da observação é obrigatória.");
        }

        Reporte reporte = getReporteOrThrow(reporteId);

        nova.setId(null); // garante INSERT
        if (nova.getDataMensagem() == null) {
            nova.setDataMensagem(LocalDateTime.now());
        }

        reporte.getListObservacoes().add(nova);
        // Cascade.ALL + orphanRemoval fazem o trabalho sujo
        reporteRepository.save(reporte);

        return nova; // já volta com ID preenchido
    }

    // Atualiza o texto de uma observação específica de um reporte
    public Observacao atualizarMensagem(Long reporteId, Long observacaoId, String novaMensagem) {
        if (novaMensagem == null || novaMensagem.isBlank()) {
            throw new IllegalArgumentException("Mensagem da observação é obrigatória.");
        }

        Reporte reporte = getReporteOrThrow(reporteId);

        Observacao alvo = reporte.getListObservacoes().stream()
                .filter(o -> observacaoId.equals(o.getId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Observação não encontrada para este reporte: idObs=" + observacaoId));

        alvo.setMensagem(novaMensagem);
        alvo.setDataMensagem(LocalDateTime.now()); // opcional: registra edição

        reporteRepository.save(reporte);
        return alvo;
    }

    // Remove uma observação específica de um reporte
    public void remover(Long reporteId, Long observacaoId) {
        Reporte reporte = getReporteOrThrow(reporteId);

        boolean removed = reporte.getListObservacoes()
                .removeIf(o -> observacaoId.equals(o.getId()));

        if (!removed) {
            throw new EntityNotFoundException(
                    "Observação não encontrada para este reporte: idObs=" + observacaoId);
        }

        // orphanRemoval = true => JPA deleta o registro órfão
        reporteRepository.save(reporte);
    }
    
    

    // ==== Helper interno ====
    private Reporte getReporteOrThrow(Long reporteId) {
        return reporteRepository.findById(reporteId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Reporte não encontrado: id=" + reporteId));
    }
}
