package com.taelmeireles.minhasfinancas.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.taelmeireles.minhasfinancas.exception.AutenticacaoException;
import com.taelmeireles.minhasfinancas.exception.RegraNegocioException;
import com.taelmeireles.minhasfinancas.model.Usuario;
import com.taelmeireles.minhasfinancas.repository.UsuarioRepository;
import com.taelmeireles.minhasfinancas.service.UsuarioService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Usuario authenticate(String email, String senha) {
        
        Optional<Usuario> usuarioOpt = this.usuarioRepository.findByEmail(email);

        if(usuarioOpt.isEmpty() || Objects.isNull(usuarioOpt.get().getEmail())) {
            throw new AutenticacaoException("Usuário não encontrado pelo email informado.");
        }
        
        if(!usuarioOpt.get().getSenha().equals(senha)) {
            throw new AutenticacaoException("Senha inválida.");
        }

        return usuarioOpt.get();

    }
    
    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return this.usuarioRepository.save(usuario);
    }
    
    @Override
    public void validarEmail(String email) {
        boolean isEmailExist = this.usuarioRepository.existsByEmail(email);

        if(isEmailExist) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
        }
        
    }
    


}
