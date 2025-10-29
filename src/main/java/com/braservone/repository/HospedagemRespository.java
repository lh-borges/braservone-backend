package com.braservone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.braservone.models.Hospedagem;

@Repository
public interface HospedagemRespository extends JpaRepository<Hospedagem, Long>{

}
