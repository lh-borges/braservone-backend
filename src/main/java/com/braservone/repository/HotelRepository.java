package com.braservone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.braservone.models.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

}
