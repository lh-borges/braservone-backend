// src/main/java/com/projetopetroleo/repository/FornecedorRepository.java
package com.braservone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.braservone.models.Fornecedor;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    boolean existsByNomeIgnoreCase(String nome);
    List<Fornecedor> findByNomeContainingIgnoreCase(String q);
}