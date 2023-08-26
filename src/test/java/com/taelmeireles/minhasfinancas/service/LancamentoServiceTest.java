package com.taelmeireles.minhasfinancas.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import com.taelmeireles.minhasfinancas.dto.LancamentoDto;
import com.taelmeireles.minhasfinancas.enums.StatusLancamento;
import com.taelmeireles.minhasfinancas.enums.TipoLancamento;
import com.taelmeireles.minhasfinancas.exception.RegraNegocioException;
import com.taelmeireles.minhasfinancas.mapper.LancamentoMapper;
import com.taelmeireles.minhasfinancas.model.Lancamento;
import com.taelmeireles.minhasfinancas.model.Usuario;
import com.taelmeireles.minhasfinancas.repository.LancamentoRepository;
import com.taelmeireles.minhasfinancas.repository.UsuarioRepository;
import com.taelmeireles.minhasfinancas.util.LancamentoUtil;
import com.taelmeireles.minhasfinancas.util.UsuarioUtil;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
class LancamentoServiceTest {
    
    @Autowired
    private LancamentoService service;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @SpyBean
    private UsuarioService usuarioService;
    
    @SpyBean
    private LancamentoRepository lancamentoRepository;

    @Test
    void testAtualizar_DeveAtualizarLancamento_QuandoLancamentoExistirId() {
        Usuario usuario = UsuarioUtil.getUsuario();
        this.usuarioRepository.save(usuario);
        
        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento.setUsuario(usuario);
        
        this.lancamentoRepository.save(lancamento);

        Lancamento lacamentoSalvo = this.lancamentoRepository.findById(lancamento.getId()).get();
        lacamentoSalvo.setMes(3);
        lacamentoSalvo.setStatus(StatusLancamento.EFETIVADO);

        LancamentoDto lancamentoAtualizado = this.service.atualizar(LancamentoMapper.fromEntityToDto(lacamentoSalvo));

        Assertions.assertEquals(lacamentoSalvo.getMes(), lancamentoAtualizado.getMes());
        Assertions.assertEquals(lacamentoSalvo.getStatus(), lancamentoAtualizado.getStatus());

        Mockito.verify(this.lancamentoRepository, times(2)).save(any());
        Mockito.verify(this.usuarioService, times(1)).findById(any());

    }

    @Test
    void testAtualizar_DeveRetornarException_QuandoLancamentoNaoExistirId() {
        Usuario usuario = UsuarioUtil.getUsuario();
        this.usuarioRepository.save(usuario);

        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento.setUsuario(usuario);
        
        this.lancamentoRepository.save(lancamento);

        Lancamento lacamentoSalvo = this.lancamentoRepository.findById(lancamento.getId()).get();
        lacamentoSalvo.setId(null);

        Assertions.assertThrows(Exception.class, 
            () -> this.service.atualizar(LancamentoMapper.fromEntityToDto(lacamentoSalvo)));

        Mockito.verify(this.lancamentoRepository, times(1)).save(any());
        Mockito.verify(this.usuarioService, times(0)).findById(any());

    }

    @Test
    void testAtualizarStatus() {

        Assertions.assertTrue(false);
    }

    @Test
    void testBuscar_DeveRetornarUmaListaFiltrada_QuandoTiverLancamentoNaBase() {
        Usuario usuario = UsuarioUtil.getUsuario();
        this.usuarioRepository.save(usuario);

        Lancamento lancamentoDespesa = LancamentoUtil.getLancamento();
        lancamentoDespesa.setUsuario(usuario);
        lancamentoDespesa.setMes(1);
        lancamentoDespesa.setAno(2023);
        lancamentoDespesa.setValor(BigDecimal.valueOf(500));
        lancamentoDespesa.setTipo(TipoLancamento.DESPESA);
        lancamentoDespesa.setStatus(StatusLancamento.EFETIVADO);

        Lancamento lancamentoReceita = LancamentoUtil.getLancamento();
        lancamentoReceita.setUsuario(usuario);
        lancamentoReceita.setMes(1);
        lancamentoReceita.setAno(2023);
        lancamentoReceita.setValor(BigDecimal.valueOf(1000));
        lancamentoReceita.setTipo(TipoLancamento.RECEITA);
        lancamentoReceita.setStatus(StatusLancamento.EFETIVADO);

        Lancamento lancamentoReceita2 = LancamentoUtil.getLancamento();
        lancamentoReceita2.setUsuario(usuario);
        lancamentoReceita2.setMes(2);
        lancamentoReceita2.setAno(2022);
        lancamentoReceita2.setValor(BigDecimal.valueOf(1000));
        lancamentoReceita2.setTipo(TipoLancamento.RECEITA);
        lancamentoReceita2.setStatus(StatusLancamento.EFETIVADO);

        this.lancamentoRepository.save(lancamentoDespesa);
        this.lancamentoRepository.save(lancamentoReceita);
        this.lancamentoRepository.save(lancamentoReceita2);

        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setUsuario(usuario);
        lancamentoFiltro.setMes(2);

        List<LancamentoDto> lancamentoDtoList = this.service.buscar(LancamentoMapper.fromEntityToDto(lancamentoFiltro));

        Assertions.assertNotNull(lancamentoDtoList);
        Assertions.assertEquals(1, lancamentoDtoList.size());

        Mockito.verify(this.usuarioService, times(1)).findById(any());
        
    }   

    @Test
    void testBuscarPorId() {

        Assertions.assertTrue(false);
    }

    @Test
    void testDeletar_DeveDeletarUmLancamento_QuandoLancamentoExistirId() {
        Usuario usuario = UsuarioUtil.getUsuario();
        this.usuarioRepository.save(usuario);

        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento.setUsuario(usuario); 

        this.lancamentoRepository.save(lancamento);

        Assertions.assertDoesNotThrow(() -> this.service.deletar(lancamento.getId()));

        Mockito.verify(this.lancamentoRepository, times(1)).delete(any());

    }   

    @Test
    void testDeletar_DeveRetornarException_QuandoLancamentoNaoExistirId() {
        Usuario usuario = UsuarioUtil.getUsuario();
        this.usuarioRepository.save(usuario);

        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento.setUsuario(usuario); 

        this.lancamentoRepository.save(lancamento);
        lancamento.setId(UUID.randomUUID());

        Assertions.assertThrows(Exception.class, 
            () -> this.service.deletar(lancamento.getId()));
        
        Mockito.verify(this.lancamentoRepository, times(0)).delete(any());

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

        Mockito.verify(this.usuarioService, times(1)).findById(any());
        Mockito.verify(this.lancamentoRepository, times(1)).save(any());

    }

    @Test
    void testSalvar_DeveRetornarException_QuandoLancamentoExistirId() {
        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento.setUsuario(new Usuario());
        lancamento.setId(UUID.randomUUID());

        Assertions.assertThrowsExactly(RegraNegocioException.class, 
            () -> this.service.salvar(LancamentoMapper.fromEntityToDto(lancamento)));
        
        Mockito.verify(this.usuarioService, times(0)).findById(any());
        Mockito.verify(this.lancamentoRepository, times(0)).save(any());

    }

    @Test
    void testSalvar_DeveRetornarException_QuandoUsuarioNaoExistirId() {
        Usuario usuario = UsuarioUtil.getUsuario();
        
        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento.setUsuario(usuario);
        
        this.usuarioRepository.save(usuario);
        
        usuario.setId(UUID.randomUUID());
        
        lancamento.setUsuario(usuario);

        Assertions.assertThrowsExactly(RegraNegocioException.class, 
            () -> this.service.salvar(LancamentoMapper.fromEntityToDto(lancamento)));

        Mockito.verify(this.usuarioService, times(1)).findById(any());
        Mockito.verify(this.lancamentoRepository, times(0)).save(any());

    }

}
