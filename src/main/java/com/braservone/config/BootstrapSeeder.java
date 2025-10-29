// src/main/java/com/projetopetroleo/config/BootstrapSeeder.java
package com.braservone.config;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braservone.enums.ERole;
import com.braservone.models.Account;
import com.braservone.models.Empresa;
import com.braservone.models.Role;
import com.braservone.models.User;
import com.braservone.repository.AccountRepository;
import com.braservone.repository.EmpresaRepository;
import com.braservone.repository.RoleRepository;
import com.braservone.repository.UserRepository;

@Service
public class BootstrapSeeder {
  private static final Logger log = LoggerFactory.getLogger(BootstrapSeeder.class);

  @Transactional
  public void run(boolean enabled,
                  String adminUsername, String adminCpf, String adminPassword,
                  String adminEmail, String adminNome,
                  String empresaNome, String empresaCnpj,
                  EmpresaRepository empresaRepository,
                  RoleRepository roleRepository,
                  UserRepository userRepository,
                  AccountRepository accountRepository,
                  PasswordEncoder passwordEncoder) {

    if (!enabled) { log.info("[BOOTSTRAP] desabilitado"); return; }

    seedRoles(roleRepository);

    Empresa empresa = upsertEmpresaByCnpj(empresaRepository, empresaCnpj, empresaNome);

    // Buscar o User já com roles para não estourar LAZY
    User admin = userRepository.findByIdWithRoles(adminUsername)
      .orElseGet(() -> upsertUserByUsername(userRepository, adminUsername, adminEmail, adminCpf, adminNome, empresa));

    Role roleAdmin  = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow();
    Role roleMaster = roleRepository.findByName(ERole.ROLE_MASTER).orElseThrow();

    if (admin.getRoles() == null || admin.getRoles().isEmpty()) {
      admin.setRoles(Set.of(roleAdmin, roleMaster));
      userRepository.save(admin);
      log.info("[BOOTSTRAP] roles aplicadas ao user {}: ADMIN, MASTER", admin.getUsername());
    }

    accountRepository.findById(admin.getUsername()).ifPresentOrElse(acc -> {
      if (!acc.isEnabled()) {
        acc.setEnabled(true);
        accountRepository.save(acc);
        log.info("[BOOTSTRAP] account {} reativada", acc.getUsername());
      } else {
        log.info("[BOOTSTRAP] account já existente e ativa: {}", acc.getUsername());
      }
    }, () -> {
      try {
    	  User managedAdmin = userRepository.getReferenceById(admin.getUsername());

    	// NÃO setar a PK manualmente; com @MapsId o Hibernate deriva do User ao persistir
    	Account acc = new Account();
    	acc.setUser(managedAdmin);                  // @MapsId: PK virá daqui
    	acc.setEnabled(true);
    	acc.setPassword(passwordEncoder.encode(adminPassword));

    	// save() agora fará PERSIST (id nulo) ao invés de MERGE
    	accountRepository.save(acc);
    	log.info("[BOOTSTRAP] account criada: {}", managedAdmin.getUsername());
      } catch (DataIntegrityViolationException ex) {
        log.warn("[BOOTSTRAP] corrida ao criar account; provavelmente já criada.");
      }
    });
  }

  /* ==== helpers transacionais ==== */

  @Transactional
  void seedRoles(RoleRepository roleRepository) {
    upsertRole(roleRepository, ERole.ROLE_USER);
    upsertRole(roleRepository, ERole.ROLE_ADMIN);
    upsertRole(roleRepository, ERole.ROLE_MASTER);
  }

  private Role upsertRole(RoleRepository roleRepository, ERole name) {
    return roleRepository.findByName(name).orElseGet(() -> {
      try { return roleRepository.save(new Role(name)); }
      catch (DataIntegrityViolationException ex) { return roleRepository.findByName(name).orElseThrow(); }
    });
  }

  private Empresa upsertEmpresaByCnpj(EmpresaRepository repo, String cnpj, String nome) {
    return repo.findByCnpj(cnpj).orElseGet(() -> {
      try {
        Empresa e = new Empresa();
        e.setNome(nome);
        e.setCnpj(cnpj);
        return repo.save(e);
      } catch (DataIntegrityViolationException ex) {
        return repo.findByCnpj(cnpj).orElseThrow();
      }
    });
  }

  private User upsertUserByUsername(UserRepository repo, String username, String email, String cpf, String nome, Empresa empresa) {
    return repo.findById(username).orElseGet(() -> {
      try {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setCpf(cpf);
        u.setNome(nome);
        u.setEmpresa(empresa);
        return repo.save(u);
      } catch (DataIntegrityViolationException ex) {
        return repo.findById(username).orElseThrow();
      }
    });
  }
}
