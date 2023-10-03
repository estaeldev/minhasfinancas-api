package com.taelmeireles.minhasfinancas.service;

public interface TokenJwtService {
    
    String gerarToken(String email);

    Boolean validarToken(String token);

    String obterLoginUsuario(String token);
    
}
