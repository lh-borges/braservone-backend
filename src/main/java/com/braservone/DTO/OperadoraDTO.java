// com/projetopetroleo/dto/OperadoraDTO.java
package com.braservone.DTO;

import com.braservone.models.Endereco;

public record OperadoraDTO(
        Long id,
        String nome,
        String siglas,
        String pais,
        Endereco endereco,
        String emailContato,
        String telefoneContato,
        String responsaveltecnico,
        Boolean ativo,
        Long empresaId
) {}
