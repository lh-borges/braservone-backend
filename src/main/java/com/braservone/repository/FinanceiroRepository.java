package com.braservone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.braservone.models.Financeiro;

@Repository
public interface FinanceiroRepository extends JpaRepository<Financeiro, Long>{

}
