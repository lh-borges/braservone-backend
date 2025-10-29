// com.projetopetroleo.api.VeiculoController
package com.braservone.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.braservone.enums.StatusVeiculos;
import com.braservone.models.Veiculo;
import com.braservone.service.VeiculoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {
	@Autowired
    private VeiculoService service;

    @GetMapping
    public List<Veiculo> listar() {
        return service.listar();
    }

    @GetMapping("/{placa}")
    public Veiculo buscar(@PathVariable String placa) {
        return service.buscarPorPlaca(placa);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Veiculo criar(@RequestBody @Valid Veiculo v) {
        return service.criar(v);
    }

    @PutMapping("/{placa}")
    public Veiculo atualizar(@PathVariable String placa, @RequestBody @Valid Veiculo v) {
        return service.atualizar(placa, v);
    }

    @PatchMapping("/{placa}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarStatus(@PathVariable String placa, @RequestParam StatusVeiculos status) {
        service.alterarStatus(placa, status);
    }

    @DeleteMapping("/{placa}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable String placa) {
        service.excluir(placa);
    }
    
    @GetMapping("/{placa}/existe")
    public boolean existe(@PathVariable String placa) {
        return service.existePorPlaca(placa);
    }
}
