package com.taelmeireles.minhasfinancas.mapper;

import com.taelmeireles.minhasfinancas.dto.UsuarioDto;
import com.taelmeireles.minhasfinancas.model.Usuario;

public class UsuarioMapper {
    
    private UsuarioMapper() {}

    public static Usuario fromDtoToEntity(UsuarioDto dto) {
        return Usuario.builder()
            .nome(dto.getNome())
            .email(dto.getEmail())
            .senha(dto.getSenha())
            .build();
    }

}
