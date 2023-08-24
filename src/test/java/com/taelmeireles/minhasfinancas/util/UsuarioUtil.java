package com.taelmeireles.minhasfinancas.util;

import java.time.LocalDate;

import com.taelmeireles.minhasfinancas.model.Usuario;

public class UsuarioUtil {
    
    private UsuarioUtil() {}

    public static Usuario getUsuario() {

        return Usuario.builder()
        .nome("usuario")
        .email("usuario@gmail.com")
        .senha("senha")
        .dataCadastro(LocalDate.now())
        .build();

    }
    

}
