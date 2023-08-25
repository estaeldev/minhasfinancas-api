package com.taelmeireles.minhasfinancas.service;

import java.util.UUID;

import com.taelmeireles.minhasfinancas.model.Usuario;

public interface UsuarioService {
    
    Usuario authenticate(String email, String senha);

    Usuario save(Usuario usuario);

    void validarEmail(String email);

    Usuario findById(UUID usuarioId);
    
}
