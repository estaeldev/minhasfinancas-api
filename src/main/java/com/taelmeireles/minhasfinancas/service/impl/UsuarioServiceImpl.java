package com.taelmeireles.minhasfinancas.service.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taelmeireles.minhasfinancas.dto.TokenJwtDto;
import com.taelmeireles.minhasfinancas.exception.AutenticacaoException;
import com.taelmeireles.minhasfinancas.exception.RegraNegocioException;
import com.taelmeireles.minhasfinancas.model.Usuario;
import com.taelmeireles.minhasfinancas.repository.UsuarioRepository;
import com.taelmeireles.minhasfinancas.service.TokenJwtService;
import com.taelmeireles.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenJwtService tokenJwtService;

    @Override
    @Transactional(readOnly = true)
    public TokenJwtDto authenticate(String email, String senha) {
        
        Optional<Usuario> usuarioOpt = this.usuarioRepository.findByEmail(email);
        
        if(usuarioOpt.isEmpty() || Objects.isNull(usuarioOpt.get().getEmail())) {
            throw new AutenticacaoException("Usuário não encontrado pelo email informado.");
        }
        
        if(!this.passwordEncoder.matches(senha, usuarioOpt.get().getSenha())) {
            throw new AutenticacaoException("Senha inválida.");
        }
        
        String token = this.tokenJwtService.gerarToken(usuarioOpt.get());

        return TokenJwtDto.builder()
            .nome(usuarioOpt.get().getNome())
            .token(token)
            .build();

    }
    
    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        validarEmail(usuario.getEmail());
        usuario.setSenha(this.passwordEncoder.encode(usuario.getSenha()));
        return this.usuarioRepository.save(usuario);
    }
    
    @Override
    public void validarEmail(String email) {
        boolean isEmailExist = this.usuarioRepository.existsByEmail(email);
        
        if(isEmailExist) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
        }
        
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario findById(UUID usuarioId) {
        Optional<Usuario> usuarioOpt = this.usuarioRepository.findById(usuarioId);
        return usuarioOpt.orElseThrow(() -> new RegraNegocioException("Usuario não encontrado pelo 'ID' informado."));
    }
    

}
