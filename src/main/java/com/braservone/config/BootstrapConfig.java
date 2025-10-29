// src/main/java/com/projetopetroleo/config/BootstrapConfig.java
package com.braservone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.braservone.repository.*;

@Configuration
public class BootstrapConfig {

  @Value("${app.bootstrap.enabled:true}") private boolean bootstrapEnabled;
  @Value("${app.bootstrap.admin.username:lhborges}") private String adminUsername;
  @Value("${app.bootstrap.admin.cpf:00000000000}") private String adminCpf;
  @Value("${app.bootstrap.admin.password:bilzao90}") private String adminPassword;
  @Value("${app.bootstrap.admin.email:admin@example.com}") private String adminEmail;
  @Value("${app.bootstrap.admin.nome:Administrador}") private String adminNome;
  @Value("${app.bootstrap.empresa.nome:Braserv Petroleo}") private String empresaNome;
  @Value("${app.bootstrap.empresa.cnpj:00000000000}") private String empresaCnpj;

  @Bean
  CommandLineRunner dataInitializer(BootstrapSeeder seeder,
                                    EmpresaRepository empresaRepository,
                                    RoleRepository roleRepository,
                                    UserRepository userRepository,
                                    AccountRepository accountRepository,
                                    PasswordEncoder passwordEncoder) {
    return args -> seeder.run(
        bootstrapEnabled,
        adminUsername, adminCpf, adminPassword, adminEmail, adminNome,
        empresaNome, empresaCnpj,
        empresaRepository, roleRepository, userRepository, accountRepository, passwordEncoder
    );
  }
}
