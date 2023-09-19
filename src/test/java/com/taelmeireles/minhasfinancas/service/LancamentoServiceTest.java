package com.taelmeireles.minhasfinancas.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.test.context.ActiveProfiles;

import com.taelmeireles.minhasfinancas.dto.LancamentoDto;
import com.taelmeireles.minhasfinancas.enums.StatusLancamento;
import com.taelmeireles.minhasfinancas.enums.TipoLancamento;
import com.taelmeireles.minhasfinancas.exception.RegraNegocioException;
import com.taelmeireles.minhasfinancas.mapper.LancamentoMapper;
import com.taelmeireles.minhasfinancas.model.Lancamento;
import com.taelmeireles.minhasfinancas.model.Usuario;
import com.taelmeireles.minhasfinancas.repository.LancamentoRepository;
import com.taelmeireles.minhasfinancas.service.impl.LancamentoServiceImpl;
import com.taelmeireles.minhasfinancas.util.LancamentoUtil;
import com.taelmeireles.minhasfinancas.util.UsuarioUtil;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class LancamentoServiceTest {

    @Mock
    private UsuarioService usuarioService;
    
    @Mock
    private LancamentoRepository lancamentoRepository;

    @InjectMocks
    private LancamentoServiceImpl lancamentoService;

    @Test
    void testAtualizar_DeveAtualizarLancamento_QuandoLancamentoExistirId() {
        Usuario usuario = UsuarioUtil.getUsuario();
        usuario.setId(UUID.randomUUID());

        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento.setId(UUID.randomUUID());
        lancamento.setUsuario(usuario);
        
        Mockito.when(this.usuarioService.findById(usuario.getId())).thenReturn(usuario);
        Mockito.when(this.lancamentoRepository.save(any(Lancamento.class))).thenReturn(lancamento); 

        LancamentoDto lancamentoAtualizado = this.lancamentoService.atualizar(LancamentoMapper.fromEntityToDto(lancamento));

        Assertions.assertNotNull(lancamentoAtualizado);

        Mockito.verify(this.lancamentoRepository, times(1)).save(any());
        Mockito.verify(this.usuarioService, times(1)).findById(any());

    }

    @Test
    void testAtualizar_DeveRetornarException_QuandoLancamentoNaoExistirId() {
        
        Assertions.assertThrows(Exception.class, 
            () -> this.lancamentoService.atualizar(LancamentoMapper.fromEntityToDto(new Lancamento())));

        Mockito.verify(this.lancamentoRepository, times(0)).save(any());
        Mockito.verify(this.usuarioService, times(0)).findById(any());

    }

    @Test
    void testAtualizarStatus_DeveRetornarVoid_QuandoAtualizacaoForBemSucedida() {
        Usuario usuario = UsuarioUtil.getUsuario();
        usuario.setId(UUID.randomUUID());

        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento.setUsuario(usuario);
        lancamento.setId(UUID.randomUUID());

        Mockito.when(this.lancamentoRepository.findById(lancamento.getId()))
            .thenReturn(Optional.of(lancamento));

        Mockito.when(this.usuarioService.findById(any(UUID.class))).thenReturn(usuario);

        Mockito.when(this.lancamentoRepository.save(any(Lancamento.class))).thenReturn(lancamento);

        Assertions.assertDoesNotThrow(() -> this.lancamentoService.atualizarStatus(lancamento.getId(), "EFETIVADO"));
        
        Mockito.verify(this.lancamentoRepository, times(1)).findById(any());
        Mockito.verify(this.usuarioService, times(1)).findById(any());
        Mockito.verify(this.lancamentoRepository, times(1)).save(any());

    }

    @Test
    void testAtualizarStatus_DeveRetornarException_QuandoStatusInformadoForErrado() {

        Assertions.assertThrows(Exception.class, 
        () -> this.lancamentoService.atualizarStatus(new Lancamento().getId(), "ERRADO"), 
        "Não foi possível atualizar o status do lançamento.");

        Mockito.verify(this.lancamentoRepository, times(0)).findById(any());
        Mockito.verify(this.usuarioService, times(0)).findById(any());
        Mockito.verify(this.lancamentoRepository, times(0)).save(any());
    }
    
    @Test
    void testAtualizarStatus_DeveRetornarException_QuandoLancamentoNaoExistirId() {

        Mockito.doThrow(RegraNegocioException.class).when(this.lancamentoRepository)
            .findById(any());
        
        Assertions.assertThrowsExactly(RegraNegocioException.class, 
            () -> this.lancamentoService.atualizarStatus(new Lancamento().getId(), StatusLancamento.PENDENTE.name()));
        
        Mockito.verify(this.lancamentoRepository, times(0)).save(any());
        Mockito.verify(this.lancamentoRepository, times(1)).findById(any());
    }

    @Test
    void testBuscar_DeveRetornarUmaListaFiltrada_QuandoTiverLancamentoNaBase() {
        Usuario usuario = UsuarioUtil.getUsuario();
        usuario.setId(UUID.randomUUID());
        
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

        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setUsuario(usuario);
        lancamentoFiltro.setMes(2);

        Example<Lancamento> example = Example.of(lancamentoFiltro, ExampleMatcher.matching()
            .withIgnorePaths("dataCadastro")
            .withIgnoreCase()
            .withStringMatcher(StringMatcher.CONTAINING));

        Mockito.when(this.usuarioService.findById(usuario.getId())).thenReturn(usuario);

        Mockito.when(this.lancamentoRepository.findAll(example))
            .thenReturn(List.of(lancamentoReceita2));
        
        List<LancamentoDto> lancamentoDtoList = this.lancamentoService.buscar(LancamentoMapper.fromEntityToDto(lancamentoFiltro));

        Assertions.assertNotNull(lancamentoDtoList);
        Assertions.assertEquals(1, lancamentoDtoList.size());

        Mockito.verify(this.usuarioService, times(1)).findById(any());
        
    }   

    @Test
    void testBuscarPorId_DeveRetornarLancamento_QuandoLancamentoExistirNaBase() {
        Usuario usuario = UsuarioUtil.getUsuario();

        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento.setId(UUID.randomUUID());
        lancamento.setUsuario(usuario);

        Mockito.when(this.lancamentoRepository.findById(any())).thenReturn(Optional.of(lancamento));

        LancamentoDto lacamentoEncontrado = this.lancamentoService.buscarPorId(lancamento.getId());

        Assertions.assertNotNull(lacamentoEncontrado);
        Assertions.assertNotNull(lacamentoEncontrado.getId());
        
        Mockito.verify(this.lancamentoRepository, times(1)).findById(any());
    }

    @Test
    void testBuscarPorId_DeveRetornarException_QuandoLancamentoNaoExistirNaBase() {
        Lancamento lancamento = LancamentoUtil.getLancamento();
        
        Assertions.assertThrowsExactly(RegraNegocioException.class, 
            () -> this.lancamentoService.buscarPorId(lancamento.getId()), 
            "Lançamento não encontrado pelo 'ID' informado.");

        Mockito.verify(this.lancamentoRepository, times(1)).findById(any());

    }

    @Test
    void testDeletar_DeveDeletarUmLancamento_QuandoLancamentoExistirId() {
        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento.setUsuario(new Usuario());
        lancamento.setId(UUID.randomUUID());

        Mockito.when(this.lancamentoRepository.findById(lancamento.getId())).thenReturn(Optional.of(lancamento));
        

        Assertions.assertDoesNotThrow(() -> this.lancamentoService.deletar(lancamento.getId()));
        
        Mockito.verify(this.lancamentoRepository, times(1)).findById(any());
        Mockito.verify(this.lancamentoRepository, times(1)).delete(any());

    }   

    @Test
    void testDeletar_DeveRetornarException_QuandoLancamentoNaoExistirId() {
        Lancamento lancamento = LancamentoUtil.getLancamento();

        Assertions.assertThrows(Exception.class, 
            () -> this.lancamentoService.deletar(lancamento.getId()));
        
        Mockito.verify(this.lancamentoRepository, times(0)).delete(any());
    }

    @Test
    void testObterSaldoPorUsuario_DeveRetornarSaldo_QuandoTudoEstiverOk() {
        Usuario usuario = UsuarioUtil.getUsuario();
        usuario.setId(UUID.randomUUID());

        Lancamento lancamentoDespesa = LancamentoUtil.getLancamento();
        lancamentoDespesa.setUsuario(usuario);
        lancamentoDespesa.setValor(BigDecimal.valueOf(500));
        lancamentoDespesa.setTipo(TipoLancamento.DESPESA);
        lancamentoDespesa.setStatus(StatusLancamento.EFETIVADO);

        Lancamento lancamentoReceita = LancamentoUtil.getLancamento();
        lancamentoReceita.setUsuario(usuario);
        lancamentoReceita.setValor(BigDecimal.valueOf(1000));
        lancamentoReceita.setTipo(TipoLancamento.RECEITA);
        lancamentoReceita.setStatus(StatusLancamento.EFETIVADO);

        Lancamento lancamentoReceita2 = LancamentoUtil.getLancamento();
        lancamentoReceita2.setUsuario(usuario);
        lancamentoReceita2.setValor(BigDecimal.valueOf(1000));
        lancamentoReceita2.setTipo(TipoLancamento.RECEITA);
        lancamentoReceita2.setStatus(StatusLancamento.EFETIVADO);

        BigDecimal totalDespesa = lancamentoDespesa.getValor();
        BigDecimal totalReceita = lancamentoReceita.getValor().add(lancamentoReceita2.getValor());

        Mockito.when(this.lancamentoRepository.obterSaldoPorTipoLancamentoEUsuario(usuario.getId(), TipoLancamento.DESPESA))
            .thenReturn(totalDespesa);

        Mockito.when(this.lancamentoRepository.obterSaldoPorTipoLancamentoEUsuario(usuario.getId(), TipoLancamento.RECEITA))
            .thenReturn(totalReceita);


        BigDecimal saldoFinal = this.lancamentoService.obterSaldoPorUsuario(usuario.getId());

        Assertions.assertEquals(1500, saldoFinal.intValue());

        Mockito.verify(this.lancamentoRepository, times(2))
            .obterSaldoPorTipoLancamentoEUsuario(any(), any());
        
    }
    
    @Test
    void testSalvar_DeveSalvarLancamento_QuandoLancamentoIdNaoExistirEUsuarioExistirId() {
        Usuario usuario = UsuarioUtil.getUsuario();
        usuario.setId(UUID.randomUUID());

        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento.setUsuario(usuario);
        
        Mockito.when(this.usuarioService.findById(usuario.getId())).thenReturn(usuario);
        Mockito.when(this.lancamentoRepository.save(lancamento)).thenReturn(lancamento);

        LancamentoDto lancamentoDtoSalvo = this.lancamentoService.salvar(LancamentoMapper.fromEntityToDto(lancamento));
        lancamentoDtoSalvo.setId(UUID.randomUUID());

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
            () -> this.lancamentoService.salvar(LancamentoMapper.fromEntityToDto(lancamento)));
        
        Mockito.verify(this.usuarioService, times(0)).findById(any());
        Mockito.verify(this.lancamentoRepository, times(0)).save(any());

    }

    @Test
    void testSalvar_DeveRetornarException_QuandoUsuarioNaoExistirId() {
        Usuario usuario = UsuarioUtil.getUsuario();
        usuario.setId(UUID.randomUUID());
        
        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento.setUsuario(usuario);

        Mockito.doThrow(RegraNegocioException.class).when(this.usuarioService).findById(usuario.getId());

        Assertions.assertThrowsExactly(RegraNegocioException.class, 
            () -> this.lancamentoService.salvar(LancamentoMapper.fromEntityToDto(lancamento)));

        Mockito.verify(this.usuarioService, times(1)).findById(any());
        Mockito.verify(this.lancamentoRepository, times(0)).save(any());

    }

}
