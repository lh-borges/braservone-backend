package com.braservone.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braservone.DTO.EstoqueQuimicoPorTipoRegiaoDTO;
import com.braservone.enums.StatusQuimicos;
import com.braservone.models.Fornecedor;
import com.braservone.models.Quimico;
import com.braservone.repository.FornecedorRepository;
import com.braservone.repository.QuimicoRepository;

import jakarta.validation.Valid;

@Service
public class QuimicoService {

    private final QuimicoRepository quimicoRepository;
    private final FornecedorRepository fornecedorRepository;

    public QuimicoService(QuimicoRepository quimicoRepository,
                          FornecedorRepository fornecedorRepository) {
        this.quimicoRepository = quimicoRepository;
        this.fornecedorRepository = fornecedorRepository;
    }

    // ========================= READ =========================

    /** Lista TODOS os químicos (qualquer status) com fornecedor carregado. */
    @Transactional(readOnly = true)
    public List<Quimico> listar() {
        return quimicoRepository.findAllFetchFornecedor();
    }

    /** Lista APENAS ATIVOS (atalho semântico). */
    @Transactional(readOnly = true)
    public List<Quimico> listarAtivos() {
        return quimicoRepository.findAllAtivosFetchFornecedor();
    }

    /** Lista por status específico (ATIVO/FINALIZADO/INATIVO), já com fornecedor. */
    @Transactional(readOnly = true)
    public List<Quimico> listarPorStatus(StatusQuimicos status) {
        return quimicoRepository.findAllByStatusFetchFornecedor(status);
    }

    /** Busca por código (qualquer status), já com fornecedor. */
    @Transactional(readOnly = true)
    public Quimico buscar(Long codigo) {
        return quimicoRepository.findOneFetchFornecedor(codigo)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                "Químico não encontrado: " + codigo));
    }

    /** Busca APENAS se estiver ATIVO. */
    @Transactional(readOnly = true)
    public Quimico buscarAtivo(Long codigo) {
        return quimicoRepository.findAtivoByCodigoFetchFornecedor(codigo)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                "Químico ativo não encontrado: " + codigo));
    }

    

    // ========================= LITE para o Front (map de lote/tipo) =========================

    /** Projeção enxuta para popular selects/labels sem depender só dos ATIVOS. */
    public record QuimicoLite(Long codigo, String lote, com.braservone.enums.TipoQuimico tipoQuimico,
                              String fornecedorNome, StatusQuimicos status) {}

    @Transactional(readOnly = true)
    public List<QuimicoLite> listarLite() {
        return quimicoRepository.findAllFetchFornecedor().stream()
            .map(q -> new QuimicoLite(
                q.getCodigo(),
                q.getLote(),
                q.getTipoQuimico(),
                q.getFornecedor() != null ? q.getFornecedor().getNome() : null,
                q.getStatusQuimicos()
            ))
            .toList();
    }

    // ========================= CREATE =========================

    @Transactional
    public Quimico criar(@Valid Quimico payload) {
        payload.setCodigo(null);
        payload.setFornecedor(resolveFornecedorFromPayload(payload.getFornecedor()));

        if (payload.getEstoqueInicial() != null && payload.getEstoqueInicial().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Estoque inicial não pode ser negativo.");
        }
        if (payload.getEstoqueInicial() == null) payload.setEstoqueInicial(BigDecimal.ZERO);

        // Defaults/validações para estoqueUtilizado
        if (payload.getEstoqueUtilizado() == null) payload.setEstoqueUtilizado(BigDecimal.ZERO);
        if (payload.getEstoqueUtilizado().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Estoque utilizado não pode ser negativo.");
        }
        // Evita saldo negativo no cadastro (sem movimentos ainda)
        if (payload.getEstoqueInicial()
                .subtract(payload.getEstoqueUtilizado())
                .compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("No cadastro, o utilizado não pode exceder o estoque inicial.");
        }

        if (payload.getStatusQuimicos() == null) payload.setStatusQuimicos(StatusQuimicos.ATIVO);

        Quimico salvo = quimicoRepository.save(payload);
        return quimicoRepository.findOneFetchFornecedor(salvo.getCodigo()).orElse(salvo);
    }

    @Transactional
    public Quimico salvar(@Valid Quimico payload) {
        return criar(payload);
    }

    // ========================= UPDATE =========================

    @Transactional
    public Quimico atualizar(Long codigo, Quimico patch) {
        Quimico entidade = quimicoRepository.findById(codigo)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                "Químico " + codigo + " não encontrado."));

        // fornecedor tratado separado
        if (patch.getFornecedor() != null) {
            Fornecedor novoFornecedor = (patch.getFornecedor().getId() == null)
                ? null
                : resolveFornecedorNullable(patch.getFornecedor().getId());
            entidade.setFornecedor(novoFornecedor);
            patch.setFornecedor(null);
        }

        if (patch.getEstoqueInicial() != null && patch.getEstoqueInicial().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Estoque inicial não pode ser negativo.");
        }
        if (patch.getEstoqueUtilizado() != null && patch.getEstoqueUtilizado().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Estoque utilizado não pode ser negativo.");
        }

        // Previsão de saldo considerando possíveis mudanças de estoqueInicial e estoqueUtilizado
        BigDecimal novoEstoqueInicial = patch.getEstoqueInicial() != null ? patch.getEstoqueInicial() : entidade.getEstoqueInicial();
        BigDecimal novoUtilizado      = patch.getEstoqueUtilizado() != null ? patch.getEstoqueUtilizado() : entidade.getEstoqueUtilizado();

        BigDecimal movLiquido = quimicoRepository.sumMovimentadoLiquido(codigo);
        if (movLiquido == null) movLiquido = BigDecimal.ZERO;

        BigDecimal saldoPrevisto = novoEstoqueInicial.add(movLiquido).subtract(novoUtilizado);
        if (saldoPrevisto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Atualização resultaria em saldo negativo (estoqueInicial + movimentos - utilizado).");
        }

        // aplica patch (inclui dataValidade e estoqueUtilizado via atualizarCom)
        entidade.atualizarCom(patch);

        Quimico atualizado = quimicoRepository.save(entidade);
        return quimicoRepository.findOneFetchFornecedor(atualizado.getCodigo()).orElse(atualizado);
    }

    // ========================= DELETE =========================

    @Transactional
    public void excluir(Long codigo) {
        var movLiquido = quimicoRepository.sumMovimentadoLiquido(codigo);
        if (movLiquido != null && movLiquido.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Não é possível excluir: há movimentações registradas.");
        }
        quimicoRepository.deleteById(codigo);
    }

    // ========================= AGRUPAMENTOS / RELATÓRIOS =========================

    @Transactional(readOnly = true)
    public List<EstoqueQuimicoPorTipoRegiaoDTO> listarEstoqueAgrupadoPorTipoEEstado() {
        return quimicoRepository.listarEstoqueAgrupadoPorTipoEEstado();
    }

    // ========================= HELPERS =========================

    private Fornecedor resolveFornecedorNullable(Long id) {
        if (id == null) return null;
        return fornecedorRepository.findById(id)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                "Fornecedor não encontrado: " + id));
    }

    private Fornecedor resolveFornecedorFromPayload(Fornecedor in) {
        if (in == null) return null; // se optional=false na entidade, valide antes
        if (in.getId() == null) return null;
        return resolveFornecedorNullable(in.getId());
    }
}
