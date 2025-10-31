// com/projetopetroleo/mapper/OperadoraMapper.java
package com.braservone.mapper;

import com.braservone.DTO.OperadoraDTO;
import com.braservone.models.Operadora;

public final class OperadoraMapper {
    private OperadoraMapper() {}

    public static OperadoraDTO toDTO(Operadora o) {
        return new OperadoraDTO(
                o.getId(),
                o.getNome(),
                o.getSiglas(),
                o.getPais(),
                o.getEndereco(),
                o.getEmailContato(),
                o.getTelefoneContato(),
                o.getResponsaveltecnico(),
                o.getAtivo(), null
               
        );
    }

    public static void updateEntity(Operadora o, OperadoraDTO dto) {
        if (dto.nome() != null) o.setNome(dto.nome());
        if (dto.siglas() != null) o.setSiglas(dto.siglas());
        if (dto.pais() != null) o.setPais(dto.pais());
        if (dto.endereco() != null) o.setEndereco(dto.endereco());
        if (dto.emailContato() != null) o.setEmailContato(dto.emailContato());
        if (dto.telefoneContato() != null) o.setTelefoneContato(dto.telefoneContato());
        if (dto.responsaveltecnico() != null) o.setResponsaveltecnico(dto.responsaveltecnico());
        if (dto.ativo() != null) o.setAtivo(dto.ativo());
    }
}
