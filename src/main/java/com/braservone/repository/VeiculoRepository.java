// com.projetopetroleo.repository.VeiculoRepository
package com.braservone.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.braservone.models.Veiculo;

public interface VeiculoRepository extends JpaRepository<Veiculo, String> {
	boolean existsByPlacaIgnoreCase(String placa);
}
