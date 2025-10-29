package com.braservone.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.braservone.enums.ERole;
import com.braservone.models.Account;
import com.braservone.models.Role;
import com.braservone.models.User;
import com.braservone.repository.AccountRepository;
import com.braservone.repository.RoleRepository;
import com.braservone.repository.UserRepository;
import com.braservone.security.jwt.CurrentUser;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;

    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          CurrentUser currentUser) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public Optional<Account> getByUsername(String username) {
        return accountRepository.findById(username);
    }

    /** Habilita login para um perfil existente (cria Account 1–1) e aplica roles no USER */
    @Transactional
    public Account enableLogin(String username, String rawPassword, Set<ERole> roleNames) {
        User user = userRepository.findById(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (accountRepository.existsById(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já habilitado");
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Senha muito curta");
        }

        // Resolve roles (para o USER)
        Set<Role> rolesToAssign = resolveRoles(roleNames);
        // aplica no usuário (fonte da autoridade)
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        user.getRoles().addAll(rolesToAssign);
        userRepository.save(user); // garante persistência das roles no user

        // Cria a Account (apenas senha/enable)
        Account acc = new Account();
        acc.setUsername(username);
        acc.setUser(user);
        acc.setEnabled(true);
        acc.setPassword(passwordEncoder.encode(rawPassword));

        return accountRepository.save(acc);
    }

    /** Remove a conta (usuário continua existindo, mas sem login) */
    @Transactional
    public void disableLogin(String username) {
        if (!accountRepository.existsById(username)) return; // idempotente
        accountRepository.deleteById(username);
    }

    /** Troca a senha da PRÓPRIA conta do usuário autenticado */
    @Transactional
    public void changeMyPassword(String oldPassword, String newPassword) {
        final String username = currentUser.username();
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não autenticado");
        }
        changePassword(username, oldPassword, newPassword);
    }

    /** Troca de senha para um username específico (valida oldPassword) */
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Account acc = accountRepository.findById(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));

        if (!passwordEncoder.matches(oldPassword, acc.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Nova senha muito curta");
        }
        if (passwordEncoder.matches(newPassword, acc.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Nova senha não pode ser igual à atual");
        }

        acc.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(acc);
    }

    /** Admin ajusta roles — agora no USER (não no Account) */
    @Transactional
    public void setRoles(String username, Set<ERole> roleNames) {
        User user = userRepository.findById(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Set<Role> rolesToAssign = resolveRoles(roleNames);
        user.setRoles(rolesToAssign);
        userRepository.save(user);
    }

    /* ------------------------ helpers ------------------------ */

    private Set<Role> resolveRoles(Set<ERole> roleNames) {
        Set<Role> roles = new HashSet<>();
        // default: ROLE_USER se não vier nada
        if (roleNames == null || roleNames.isEmpty()) {
            roleNames = Set.of(ERole.ROLE_USER);
        }
        for (ERole rn : roleNames) {
            // Preferindo assinatura findByName(ERole). Se seu repo ainda usa String, troque para rn.name().
            Role role = roleRepository.findByName(rn)
                .orElseGet(() ->
                    // fallback opcional: tenta por String se existir um método assim no seu repo
                    roleRepository.findByName(rn) // <-- mantenha esta linha se já atualizou o repo p/ ERole
                        .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Role inválida: " + rn
                        ))
                );
            roles.add(role);
        }
        return roles;
    }
}
