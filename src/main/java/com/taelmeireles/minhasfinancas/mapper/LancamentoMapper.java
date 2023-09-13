package com.taelmeireles.minhasfinancas.mapper;

import com.taelmeireles.minhasfinancas.dto.LancamentoDto;
import com.taelmeireles.minhasfinancas.model.Lancamento;
import com.taelmeireles.minhasfinancas.model.Usuario;

public class LancamentoMapper {
    
    private LancamentoMapper() {}

    public static Lancamento fromDtoToEntity(LancamentoDto dto, Usuario usuario) {

        return Lancamento.builder()
            .id(dto.getId())
            .descricao(dto.getDescricao())
            .ano(dto.getAno())
            .mes(dto.getMes())
            .valor(dto.getValor())
            .tipo(dto.getTipo())
            .status(dto.getStatus())
            .usuario(usuario)
            .build();
    }

    public static LancamentoDto fromEntityToDto(Lancamento lancamento) {

        return LancamentoDto.builder()
            .id(lancamento.getId())
            .descricao(lancamento.getDescricao())
            .ano(lancamento.getAno())
            .mes(lancamento.getMes())
            .valor(lancamento.getValor())
            .tipo(lancamento.getTipo())
            .status(lancamento.getStatus())
            .usuarioId(lancamento.getUsuario().getId())
            .build();
    }

}
