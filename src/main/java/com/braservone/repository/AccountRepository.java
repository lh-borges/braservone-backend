package com.braservone.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.braservone.models.Account;

public interface AccountRepository extends JpaRepository<Account, String> {
  @EntityGraph(attributePaths = {"user", "roles"})
  Optional<Account> findByUsername(String username);
}
