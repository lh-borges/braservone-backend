package com.braservone.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

import jakarta.persistence.EntityNotFoundException;

@Service
public class QuimicoMovimentoService {

    private final QuimicoMovimentoRepository movimentoRepo;
    private final QuimicoRepository quimicoRepo;
    private final PocoRepository pocoRepo;

    public QuimicoMovimentoService(QuimicoMovimentoRepository movimentoRepo,
                                   QuimicoRepository quimicoRepo,
                                   PocoRepository pocoRepo) {
        this.movimentoRepo = movimentoRepo;
        this.quimicoRepo = quimicoRepo;
        this.pocoRepo = pocoRepo;
    }

    @Transactional
    public QuimicoMovimento registrarPorCodigoAnp(Long quimicoCodigo,
                                                  String pocoCodigoAnp,
                                                  TipoMovimento tipo,
                                                  BigDecimal quantidade) {

        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de movimento é obrigatório.");
        }

        if (quantidade == null || quantidade.signum() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }

        quantidade = quantidade.setScale(6, RoundingMode.HALF_UP);

        Quimico quimico = quimicoRepo.findById(quimicoCodigo)
            .orElseThrow(() -> new EntityNotFoundException(
                "Químico não encontrado: " + quimicoCodigo));

        Poco poco = pocoRepo.findById(pocoCodigoAnp)
            .orElseThrow(() -> new EntityNotFoundException(
                "Poço não encontrado (codigoAnp): " + pocoCodigoAnp));

        BigDecimal saldoAtualAntes = quimicoRepo.estoqueAtual(quimicoCodigo)
            .orElse(BigDecimal.ZERO)
            .setScale(6, RoundingMode.HALF_UP);

        if (tipo == TipoMovimento.SAIDA && saldoAtualAntes.compareTo(quantidade) < 0) {
            throw new IllegalStateException(
                "Saldo insuficiente. Estoque atual: " + saldoAtualAntes.toPlainString()
            );
        }

        QuimicoMovimento mov = new QuimicoMovimento();
        mov.setQuimico(quimico);
        mov.setPoco(poco);
        mov.setTipoMovimento(tipo);
        mov.setQntMovimentada(quantidade);
        
       

        return movimentoRepo.save(mov);
    }

    @Transactional(readOnly = true)
    public List<QuimicoMovimento> listarTodos() {
    	
    	List<QuimicoMovimento> listMov = movimentoRepo.findAllFetch();
    	System.out.println(listMov.get(0).getQuimico().getLote());
        return listMov ;
    }

    @Transactional(readOnly = true)
    public List<QuimicoMovimento> listarPorTipoMovimento(TipoMovimento tipo) {
        return movimentoRepo.findByTipoMovimentoFetch(tipo);
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
