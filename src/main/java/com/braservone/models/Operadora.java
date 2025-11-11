// com/projetopetroleo/models/Operadora.java
package com.braservone.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "operadora")
public class Operadora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    @NotBlank
    private String nome;

    @Column(length = 16)
    private String siglas;

    private String pais;

    @Embedded
    private Endereco endereco;
    
    @NotBlank
    private String emailContato;
    @NotBlank
    private String telefoneContato;
    private String responsaveltecnico;

    @Column(nullable=false)
    private Boolean ativo = Boolean.TRUE;


    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Operadora)) return false;
        Operadora that = (Operadora) o;
        return id != null && Objects.equals(id, that.id);
    }
    @Override public int hashCode() { return 31; }
}
