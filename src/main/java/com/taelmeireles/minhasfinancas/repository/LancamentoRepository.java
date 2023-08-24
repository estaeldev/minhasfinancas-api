package com.taelmeireles.minhasfinancas.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taelmeireles.minhasfinancas.model.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, UUID> {
    
}
