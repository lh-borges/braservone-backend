package com.braservone.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserDTO {

    // Removemos @NotBlank para permitir que esse campo seja nulo (não atualizado)
    @Size(min = 2, max = 120, message = "Nome deve ter entre 2 e 120 caracteres")
    private String nome;

    // Removemos @NotBlank. Se vier nulo, ignora. Se vier preenchido, valida o formato.
    @Email(message = "E-mail inválido")
    private String email;

    public UpdateUserDTO() {}

    public UpdateUserDTO(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}