package com.taelmeireles.minhasfinancas.util;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.taelmeireles.minhasfinancas.enums.StatusLancamento;
import com.taelmeireles.minhasfinancas.enums.TipoLancamento;
import com.taelmeireles.minhasfinancas.model.Lancamento;

public class LancamentoUtil {
    
    private LancamentoUtil() {}

    public static Lancamento getLancamento() {
        
        return Lancamento.builder()
            .descricao("Descrição Teste")
            .mes(2)
            .ano(2023)
            .valor(BigDecimal.valueOf(300))
            .tipo(TipoLancamento.RECEITA)
            .status(StatusLancamento.PENDENTE)
            .dataCadastro(LocalDate.now())
            .build();
    }

}
