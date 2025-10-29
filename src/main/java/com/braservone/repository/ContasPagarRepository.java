package com.braservone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.braservone.models.ContasPagar;
@Repository
public interface ContasPagarRepository extends JpaRepository<ContasPagar, Long> {

}
