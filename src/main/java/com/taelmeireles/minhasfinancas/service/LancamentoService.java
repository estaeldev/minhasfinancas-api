package com.taelmeireles.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.taelmeireles.minhasfinancas.dto.LancamentoDto;

public interface LancamentoService {

    LancamentoDto salvar(LancamentoDto dto);

    LancamentoDto atualizar(LancamentoDto dto);

    List<LancamentoDto> buscar(LancamentoDto dto);

    LancamentoDto buscarPorId(UUID lancamentoId);

    void atualizarStatus(UUID lancamentoId, String status);
    
    void deletar(UUID lancamentoId);

    BigDecimal obterSaldoPorUsuario(UUID usuario);

}
