package com.taelmeireles.minhasfinancas.service;

import java.util.List;

import com.taelmeireles.minhasfinancas.enums.StatusLancamento;
import com.taelmeireles.minhasfinancas.model.Lancamento;

public interface LancamentoService {

    Lancamento salvar(Lancamento lancamento);

    Lancamento atualizar(Lancamento lancamento);

    List<Lancamento> buscar(Lancamento lancamentoFiltro);

    void atualizarStatus(Lancamento lancamento, StatusLancamento status);
    
    void deletar(Lancamento lancamento);

}
