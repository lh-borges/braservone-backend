package com.braservone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.braservone.models.Filial;

@Repository
public interface FilialRepository extends JpaRepository<Filial, Long> {

}
