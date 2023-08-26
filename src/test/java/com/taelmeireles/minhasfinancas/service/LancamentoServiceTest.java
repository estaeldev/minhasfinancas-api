package com.taelmeireles.minhasfinancas.service;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import com.taelmeireles.minhasfinancas.dto.LancamentoDto;
import com.taelmeireles.minhasfinancas.enums.StatusLancamento;
import com.taelmeireles.minhasfinancas.exception.RegraNegocioException;
import com.taelmeireles.minhasfinancas.mapper.LancamentoMapper;
import com.taelmeireles.minhasfinancas.model.Lancamento;
import com.taelmeireles.minhasfinancas.model.Usuario;
import com.taelmeireles.minhasfinancas.repository.UsuarioRepository;
import com.taelmeireles.minhasfinancas.util.LancamentoUtil;
import com.taelmeireles.minhasfinancas.util.UsuarioUtil;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
class LancamentoServiceTest {
    
    @Autowired
    private LancamentoService service;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Test
    void testAtualizar() {

        Assertions.assertTrue(false);
    }

    @Test
    void testAtualizarStatus() {

        Assertions.assertTrue(false);
    }

    @Test
    void testBuscar() {

        Assertions.assertTrue(false);
    }   

    @Test
    void testBuscarPorId() {

        Assertions.assertTrue(false);
    }

    @Test
    void testDeletar() {

        Assertions.assertTrue(false);
    }

    @Test
    void testObterSaldoPorUsuario() {

        Assertions.assertTrue(false);
    }

    @Test
    void testSalvar_DeveSalvarLancamento_QuandoLancamentoIdNaoExistirEUsuarioExistirId() {
        Usuario usuario = UsuarioUtil.getUsuario();
        
        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento.setUsuario(usuario);
        
        this.usuarioRepository.save(usuario);
        
        LancamentoDto lancamentoDtoSalvo = this.service.salvar(LancamentoMapper.fromEntityToDto(lancamento));

        Assertions.assertNotNull(lancamentoDtoSalvo);
        Assertions.assertNotNull(lancamentoDtoSalvo.getId());
        Assertions.assertEquals(StatusLancamento.PENDENTE, lancamentoDtoSalvo.getStatus());

    }

    @Test
    void testSalvar_DeveRetornarException_QuandoLancamentoExistirId() {
        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento.setUsuario(new Usuario());
        lancamento.setId(UUID.randomUUID());

        Assertions.assertThrowsExactly(RegraNegocioException.class, 
            () -> this.service.salvar(LancamentoMapper.fromEntityToDto(lancamento)));

    }

    @Test
    void testSalvar_DeveRetornarException_QuandoUsuarioNaoExistirId() {
        Usuario usuario = UsuarioUtil.getUsuario();
        
        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento.setUsuario(usuario);
        
        this.usuarioRepository.save(usuario);
        
        usuario.setId(null);
        
        lancamento.setUsuario(usuario);

        Assertions.assertThrowsExactly(RegraNegocioException.class, 
            () -> this.service.salvar(LancamentoMapper.fromEntityToDto(lancamento)));

    }

}
