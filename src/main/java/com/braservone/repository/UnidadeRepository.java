package com.braservone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.braservone.models.Unidade;

@Repository
public interface UnidadeRepository extends JpaRepository<Unidade, String> {

}
