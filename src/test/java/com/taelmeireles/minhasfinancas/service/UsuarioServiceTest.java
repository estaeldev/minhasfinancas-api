package com.taelmeireles.minhasfinancas.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.taelmeireles.minhasfinancas.exception.AutenticacaoException;
import com.taelmeireles.minhasfinancas.exception.RegraNegocioException;
import com.taelmeireles.minhasfinancas.model.Usuario;
import com.taelmeireles.minhasfinancas.repository.UsuarioRepository;
import com.taelmeireles.minhasfinancas.service.impl.UsuarioServiceImpl;
import com.taelmeireles.minhasfinancas.util.UsuarioUtil;

import lombok.RequiredArgsConstructor;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@RequiredArgsConstructor
class UsuarioServiceTest {
    
    @InjectMocks  
    @Spy
    private UsuarioServiceImpl usuarioService;
    
    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void testAuthenticate_DeveRetornarUsuario_QuandoEmailExistirNaBaseESenhaConferir() {
        Usuario usuario = UsuarioUtil.getUsuario();
        usuario.setId(UUID.randomUUID());

        Mockito.when(this.usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        Usuario usuarioRetornado = this.usuarioService.authenticate(usuario.getEmail(), usuario.getSenha());

        Assertions.assertNotNull(usuarioRetornado);
        Assertions.assertNotNull(usuarioRetornado.getId());
        Assertions.assertDoesNotThrow(() -> this.usuarioService.authenticate(usuario.getEmail(), usuario.getSenha()));

        Mockito.verify(this.usuarioRepository, times(2)).findByEmail(any());

    }

    @Test
    void testAuthenticate_DeveRetornarException_QuandoEmailNaoExistirNaBase() {
        Usuario usuario = UsuarioUtil.getUsuario();

        Mockito.when(this.usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(AutenticacaoException.class, 
            () -> this.usuarioService.authenticate(usuario.getEmail(), usuario.getSenha()), 
            "Usuário não encontrado pelo email informado.");

        Mockito.verify(this.usuarioRepository, times(1)).findByEmail(any());

    }

    @Test
    void testAuthenticate_DeveRetornarException_QuandoEmailExistirNaBaseESenhaNaoConferir() {
        Usuario usuario = UsuarioUtil.getUsuario();

        Mockito.when(this.usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        Assertions.assertThrowsExactly(AutenticacaoException.class, 
            () -> this.usuarioService.authenticate(usuario.getEmail(), "senhaerrada"), 
            "Senha inválida.");
        
        Mockito.verify(this.usuarioRepository, times(1)).findByEmail(any());

    }

    @Test
    void testSave_DeveRetornarUsuarioSalvo_QuandoEmailExistirNaBase() {
        Usuario usuario = UsuarioUtil.getUsuario();
        usuario.setId(UUID.randomUUID());

        Mockito.doNothing().when(this.usuarioService).validarEmail(any());
        Mockito.when(this.usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        
        Usuario usuarioSalvo = this.usuarioService.save(usuario);

        Assertions.assertNotNull(usuarioSalvo);
        Assertions.assertNotNull(usuarioSalvo.getId());
        Assertions.assertNotNull(usuarioSalvo.getDataCadastro());

        Mockito.verify(this.usuarioRepository, times(1)).save(any());
        Mockito.verify(this.usuarioService, times(1)).validarEmail(any());
    }

    @Test
    void testSave_DeveRetornarException_QuandoEmailExistirNaBase() {
        Usuario usuario = UsuarioUtil.getUsuario();
        Mockito.doThrow(RegraNegocioException.class).when(this.usuarioService).validarEmail(usuario.getEmail());
        
        Assertions.assertThrowsExactly(RegraNegocioException.class, 
            () -> this.usuarioService.save(usuario));
        
        Mockito.verify(this.usuarioRepository, times(0)).save(any());
        Mockito.verify(this.usuarioService, times(1)).validarEmail(any());
    }

    @Test
    void testValidarEmail_DeveRetornarTrue_QuandoNaoExistirEmailCadastrado() {
        Usuario usuario = UsuarioUtil.getUsuario();

        Mockito.when(this.usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(false);        

        Assertions.assertDoesNotThrow(() -> this.usuarioService.validarEmail(usuario.getEmail()));

        Mockito.verify(this.usuarioRepository, times(1)).existsByEmail(any());
    }
    
    @Test
    void testValidarEmail_DeveRetornarException_QuandoExistirEmailCadastrado() {
        Usuario usuario = UsuarioUtil.getUsuario();
        String email = usuario.getEmail();

        Mockito.when(this.usuarioRepository.existsByEmail(email)).thenReturn(true);

        Assertions.assertThrows(RegraNegocioException.class, 
            () -> this.usuarioService.validarEmail(email));
        
        Mockito.verify(this.usuarioRepository, times(1)).existsByEmail(any());

    }
    

}
