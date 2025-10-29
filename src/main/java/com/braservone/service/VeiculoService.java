// com.projetopetroleo.service.VeiculoService
package com.braservone.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braservone.enums.StatusVeiculos;
import com.braservone.models.Veiculo;
import com.braservone.repository.VeiculoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;


@Service 
public class VeiculoService {
	
	@Autowired
    private VeiculoRepository repo;

    @Transactional(readOnly = true)
    public List<Veiculo> listar() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Veiculo buscarPorPlaca(String placa) {
        return repo.findById(normaliza(placa))
                   .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado: " + placa));
    }

    @Transactional
    public Veiculo criar(@Valid Veiculo v) {
        v.setPlaca(normaliza(v.getPlaca()));
        return repo.save(v);
    }

    @Transactional
    public Veiculo atualizar(String placaPath, @Valid Veiculo dados) {
        String placa = normaliza(placaPath);
        Veiculo atual = buscarPorPlaca(placa);
        // atualiza campos permitidos
        atual.setStatus(dados.getStatus());
        atual.setTipoVeiculo(dados.getTipoVeiculo());
        atual.setAnoVeiculo(dados.getAnoVeiculo());
        return repo.save(atual);
    }

    @Transactional
    public void alterarStatus(String placa, StatusVeiculos novoStatus) {
        Veiculo v = buscarPorPlaca(placa);
        v.setStatus(novoStatus);
        repo.save(v);
    }

    @Transactional
    public void excluir(String placa) {
        repo.delete(buscarPorPlaca(placa));
    }
    
    @Transactional(readOnly = true)
    public boolean existePorPlaca(String placa) {
        String p = normaliza(placa);
        // Se o ID do Veiculo for a placa, use existsById:
        if (hasExistsById()) {
            return repo.existsById(p);
        }
        System.out.println(placa);
        // Alternativa: método derivado, ignorando case
        return repo.existsByPlacaIgnoreCase(p);
    }

    private String normaliza(String placa) {
        return placa == null ? null : placa.trim().toUpperCase();
    }

    // truque simples para decidir qual chamar — se não quiser esse “if”, remova e use direto o que preferir
    private boolean hasExistsById() {
        // Ajuste se quiser eliminar esse método de “detecção”; deixei para te mostrar os dois caminhos.
        return true;
    }

  
}
