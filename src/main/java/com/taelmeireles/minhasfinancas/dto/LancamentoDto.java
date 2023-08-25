package com.taelmeireles.minhasfinancas.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.taelmeireles.minhasfinancas.enums.StatusLancamento;
import com.taelmeireles.minhasfinancas.enums.TipoLancamento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LancamentoDto {
    
    private UUID id;

    private String descricao;

    private Integer mes;

    private Integer ano;

    private BigDecimal valor;

    private TipoLancamento tipo;

    private StatusLancamento status;
    
    private UUID usuarioId;

}
