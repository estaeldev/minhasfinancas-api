package com.taelmeireles.minhasfinancas.service;

import com.taelmeireles.minhasfinancas.model.Usuario;

public interface TokenJwtService {
    
    String gerarToken(Usuario usuario);

    Boolean isTokenValido(String token);

    String obterLoginUsuario(String token);
    
}
