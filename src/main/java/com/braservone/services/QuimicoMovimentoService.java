package com.braservone.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
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

        // 1. Validações Iniciais
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de movimento é obrigatório.");
        }

        if (quantidade == null || quantidade.signum() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }

        // Garante precisão de 6 casas decimais
        quantidade = quantidade.setScale(6, RoundingMode.HALF_UP);

        // 2. Busca e Otimização: Evitar chamadas desnecessárias ao banco (findById.get())
        Quimico quimico = quimicoRepo.findById(quimicoCodigo)
            .orElseThrow(() -> new EntityNotFoundException(
                "Químico não encontrado: " + quimicoCodigo));

        Poco poco = pocoRepo.findById(pocoCodigoAnp)
            .orElseThrow(() -> new EntityNotFoundException(
                "Poço não encontrado (codigoAnp): " + pocoCodigoAnp));

        // 3. Cálculo do Saldo ATUAL e Validação de SAÍDA
        // Saldo = Estoque Inicial - Estoque Utilizado
        BigDecimal estoqueInicial = quimico.getEstoqueInicial() != null ? quimico.getEstoqueInicial() : BigDecimal.ZERO;
        BigDecimal estoqueUtilizado = quimico.getEstoqueUtilizado() != null ? quimico.getEstoqueUtilizado() : BigDecimal.ZERO;

        BigDecimal saldoAtual = estoqueInicial.subtract(estoqueUtilizado);

        if (tipo == TipoMovimento.SAIDA && saldoAtual.compareTo(quantidade) < 0) {
            throw new IllegalStateException(
                "Saldo insuficiente. Estoque atual: " + saldoAtual.toPlainString()
            );
        }
        
        // 4. ATUALIZAÇÃO DA ENTIDADE QUÍMICA
        // **********************************************
        // Lógica Corrigida:
        // SAÍDA -> Incrementa Estoque Utilizado (Consumo)
        // ENTRADA -> Incrementa Estoque Inicial (Compra/Reposição)
        if (tipo == TipoMovimento.SAIDA) {
            // Correção: Use um método set/adicionar adequado ao contexto de saída
            BigDecimal novoUtilizado = estoqueUtilizado.add(quantidade);
            quimico.setEstoqueUtilizado(novoUtilizado);
        } else { // ENTRADA
            // Correção: Incrementa o Estoque Inicial
            BigDecimal novoInicial = estoqueInicial.add(quantidade);
            quimico.setEstoqueInicial(novoInicial);
        }
        // **********************************************

        // 5. Criação e Registro do Movimento
        QuimicoMovimento mov = new QuimicoMovimento();
        mov.setQuimico(quimico); // A entidade 'quimico' já está no contexto de persistência (@Transactional)
        mov.setPoco(poco);
        mov.setTipoMovimento(tipo);
        mov.setQntMovimentada(quantidade);
        
        // O JpaRepository fará o 'quimicoRepo.save(quimico)' implicitamente
        // se Quimico for atualizado no mesmo contexto @Transactional (recommended)

        return movimentoRepo.save(mov);
    }

    @Transactional(readOnly = true)
    public List<QuimicoMovimento> listarTodos() {
    	
    	List<QuimicoMovimento> listMov = movimentoRepo.findAllFetch();
    	System.out.println(listMov.get(0).getQuimico().getLote());
        return listMov ;
    }
    

    @Transactional
    public void deletarMovimento(Long id) {
        
        var movimento = movimentoRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Movimento não encontrado para o id: " + id));

        
        Quimico quimico = movimento.getQuimico();      
        BigDecimal quantidade = movimento.getQntMovimentada(); 

        if (quimico != null && quantidade != null) {

            quimico.removerEstoque(quantidade);
            quimicoRepo.save(quimico);
        }
        
        movimentoRepo.delete(movimento);
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
