package com.braservone.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braservone.enums.TipoMovimento;
import com.braservone.enums.TipoQuimico;
import com.braservone.models.Poco;
import com.braservone.models.Quimico;
import com.braservone.models.QuimicoMovimento;
import com.braservone.repository.PocoRepository;
import com.braservone.repository.QuimicoMovimentoRepository;
import com.braservone.repository.QuimicoRepository;

@Service
public class QuimicoMovimentoService {

    private final QuimicoMovimentoRepository movimentoRepo;
    private final QuimicoRepository quimicoRepo;
    private final PocoRepository pocoRepo; // JpaRepository<Poco, String>

    public QuimicoMovimentoService(QuimicoMovimentoRepository movimentoRepo,
                                   QuimicoRepository quimicoRepo,
                                   PocoRepository pocoRepo) {
        this.movimentoRepo = movimentoRepo;
        this.quimicoRepo = quimicoRepo;
        this.pocoRepo = pocoRepo;
    }

    /**
     * Registrar movimento usando o PK do Poço: codigoAnp (String).
     */
    @Transactional
    public QuimicoMovimento registrarPorCodigoAnp(Long quimicoCodigo,
                                                  String pocoCodigoAnp,
                                                  TipoMovimento tipo,
                                                  BigDecimal quantidade) {

        if (quantidade == null || quantidade.signum() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }

        // Químico
        Quimico quimico = quimicoRepo.findById(quimicoCodigo)
            .orElseThrow(() -> new IllegalArgumentException("Químico não encontrado: " + quimicoCodigo));

        // Poço (PK = codigoAnp String)
        Poco poco = pocoRepo.findById(pocoCodigoAnp)
            .orElseThrow(() -> new IllegalArgumentException("Poço não encontrado (codigoAnp): " + pocoCodigoAnp));

        // valida saldo para SAÍDA
        BigDecimal estoqueAtual = quimicoRepo.estoqueAtual(quimicoCodigo)
            .orElseThrow(() -> new IllegalArgumentException("Químico não encontrado: " + quimicoCodigo));

        if (tipo == TipoMovimento.SAIDA && estoqueAtual.compareTo(quantidade) < 0) {
            throw new IllegalStateException("Saldo insuficiente. Estoque atual: " + estoqueAtual);
        }

        QuimicoMovimento mov = new QuimicoMovimento();
        mov.setQuimico(quimico);
        mov.setPoco(poco);
        mov.setTipoMovimento(tipo);
        mov.setQntMovimentada(quantidade);

        return movimentoRepo.save(mov);
    }

    // ----------------- Consultas (usadas pelo controller) -----------------

    @Transactional(readOnly = true)
    public List<QuimicoMovimento> listarTodos() {
        // Usa consulta com JOIN FETCH para materializar quimico/fornecedor/poco
        return movimentoRepo.findAllFetch();
    }

    @Transactional(readOnly = true)
    public List<QuimicoMovimento> listarPorPocoCodigoAnp(String codigoAnp) {
        return movimentoRepo.findByPocoCodigoAnpFetch(codigoAnp);
    }

    @Transactional(readOnly = true)
    public List<QuimicoMovimento> listarPorQuimico(Long quimicoCodigo) {
        return movimentoRepo.findByQuimicoCodigoFetch(quimicoCodigo);
    }

    @Transactional(readOnly = true)
    public List<QuimicoMovimento> listarPorTipo(TipoQuimico tipo) {
        return movimentoRepo.findByTipoQuimicoFetch(tipo);
    }
}
