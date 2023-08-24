package com.taelmeireles.minhasfinancas.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.taelmeireles.minhasfinancas.enums.StatusLancamento;
import com.taelmeireles.minhasfinancas.enums.TipoLancamento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "lancamentos", schema = "financas")
public class Lancamento implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "descricao", nullable = true)
    private String descricao;

    @Column(name = "mes", nullable = true)
    private Integer mes;

    @Column(name = "ano", nullable = true)
    private Integer ano;

    @Column(name = "valor", nullable = true)
    private BigDecimal valor;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "tipo", nullable = true)
    private TipoLancamento tipo;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = true)
    private StatusLancamento status;

    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    

}
