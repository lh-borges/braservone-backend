package com.braservone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.braservone.models.Tampao;

@Repository
public interface TampaoRepository extends JpaRepository<Tampao, Long> {

}
