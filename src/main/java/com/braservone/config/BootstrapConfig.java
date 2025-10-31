// src/main/java/com/braservone/config/BootstrapConfig.java
package com.braservone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.braservone.repository.AccountRepository;
import com.braservone.repository.RoleRepository;
import com.braservone.repository.UserRepository;

@Configuration
public class BootstrapConfig {

    @Value("${app.bootstrap.enabled:true}")
    private boolean bootstrapEnabled;

    @Value("${app.bootstrap.admin.username:lhborges}")
    private String adminUsername;

    @Value("${app.bootstrap.admin.cpf:00000000000}")
    private String adminCpf;

    @Value("${app.bootstrap.admin.password:bilzao90}")
    private String adminPassword;

    @Value("${app.bootstrap.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.nome:Administrador}")
    private String adminNome;

    @Bean
    CommandLineRunner dataInitializer(BootstrapSeeder seeder,
                                      RoleRepository roleRepository,
                                      UserRepository userRepository,
                                      AccountRepository accountRepository,
                                      PasswordEncoder passwordEncoder) {

        return args -> seeder.run(
                bootstrapEnabled,
                adminUsername,
                adminCpf,
                adminPassword,
                adminEmail,
                adminNome,
                roleRepository,
                userRepository,
                accountRepository,
                passwordEncoder
        );
    }
}
