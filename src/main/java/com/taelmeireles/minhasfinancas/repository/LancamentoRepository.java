package com.taelmeireles.minhasfinancas.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taelmeireles.minhasfinancas.enums.TipoLancamento;
import com.taelmeireles.minhasfinancas.model.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, UUID> {
    
    @Query("SELECT sum(l.valor) FROM Lancamento l JOIN l.usuario u WHERE u.id = :usuarioId AND l.tipo = :tipo GROUP BY u")
    BigDecimal obterSaldoPorTipoLancamentoEUsuaririo(@Param("usuarioId") UUID usuarioId, @Param("tipo") TipoLancamento tipo);

}
