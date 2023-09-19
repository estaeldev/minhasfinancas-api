package com.taelmeireles.minhasfinancas.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taelmeireles.minhasfinancas.dto.UsuarioDto;
import com.taelmeireles.minhasfinancas.enums.TipoLancamento;
import com.taelmeireles.minhasfinancas.exception.AutenticacaoException;
import com.taelmeireles.minhasfinancas.exception.RegraNegocioException;
import com.taelmeireles.minhasfinancas.model.Lancamento;
import com.taelmeireles.minhasfinancas.model.Usuario;
import com.taelmeireles.minhasfinancas.repository.LancamentoRepository;
import com.taelmeireles.minhasfinancas.repository.UsuarioRepository;
import com.taelmeireles.minhasfinancas.util.LancamentoUtil;
import com.taelmeireles.minhasfinancas.util.UsuarioUtil;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioControllerTest {
    
    private static final String API_URL = "/api/usuarios";
    private static final MediaType JSON_VALUE = MediaType.APPLICATION_JSON;
    
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private UsuarioRepository usuarioRepository;

    @SpyBean
    private LancamentoRepository lancamentoRepository;

    @BeforeEach
    void config() {
        this.lancamentoRepository.deleteAll();
        this.usuarioRepository.deleteAll();
    }

    @Test
    void testAutenticar_DeveRetornarUsuarioAutenticado_QuandoRequisicaoForBemSucedida() throws Exception {

        Usuario usuario = UsuarioUtil.getUsuario();
        usuario = this.usuarioRepository.save(usuario);

        UsuarioDto usuarioDto = UsuarioDto.builder()
            .email(usuario.getEmail())
            .senha(usuario.getSenha())
        .build();

        String json = new ObjectMapper().writeValueAsString(usuarioDto);
        
        this.mockMvc.perform(MockMvcRequestBuilders
            .post(API_URL.concat("/auth"))
            .accept(JSON_VALUE)
            .contentType(JSON_VALUE)
            .content(json)
        ).andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId().toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
            .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));

        Mockito.verify(this.usuarioRepository, times(1)).findByEmail(usuario.getEmail());
        
    }

    @Test
    void testAutenticar_DeveRetornarException_QuandoUsuarioNaoExistirNaBase() throws Exception {

        UsuarioDto usuarioDto = UsuarioDto.builder()
            .email("usuario@gmail.com")
            .senha("senha")
        .build();

        String json = new ObjectMapper().writeValueAsString(usuarioDto);
        
        this.mockMvc.perform(MockMvcRequestBuilders
            .post(API_URL.concat("/auth"))
            .accept(JSON_VALUE)
            .contentType(JSON_VALUE)
            .content(json)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof AutenticacaoException))
            .andExpect(result -> Assertions.assertEquals("Usuário não encontrado pelo email informado.", 
                result.getResolvedException().getMessage()));

        Mockito.verify(this.usuarioRepository, times(1)).findByEmail(any());
        
    }

    @Test
    void testAutenticar_DeveRetornarException_QuandoUsuarioExistirNaBaseESenhaIncorreta() throws Exception {

       Usuario usuario = UsuarioUtil.getUsuario();
        usuario = this.usuarioRepository.save(usuario);

        UsuarioDto usuarioDto = UsuarioDto.builder()
            .email(usuario.getEmail())
            .senha("incorreto")
        .build();
        
        String json = new ObjectMapper().writeValueAsString(usuarioDto);
        
        this.mockMvc.perform(MockMvcRequestBuilders
            .post(API_URL.concat("/auth"))
            .accept(JSON_VALUE)
            .contentType(JSON_VALUE)
            .content(json)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof AutenticacaoException))
            .andExpect(result -> Assertions.assertEquals("Senha inválida.", 
                result.getResolvedException().getMessage()));
        
        Mockito.verify(this.usuarioRepository, times(1)).findByEmail(any());
        
    }

    @Test
    void testObterSaldo_DeveRetornarSaldoPorUsuario_QuandoRequisicaoForOk() throws Exception {
        Usuario usuario = UsuarioUtil.getUsuario();
        usuario = this.usuarioRepository.save(usuario);

        Lancamento lancamento1 = LancamentoUtil.getLancamento();
        lancamento1.setUsuario(usuario);
        lancamento1.setTipo(TipoLancamento.DESPESA);
        lancamento1.setValor(BigDecimal.valueOf(500));

        Lancamento lancamento2 = LancamentoUtil.getLancamento();
        lancamento2.setUsuario(usuario);
        lancamento2.setTipo(TipoLancamento.RECEITA);
        lancamento2.setValor(BigDecimal.valueOf(1000));
        
        this.lancamentoRepository.save(lancamento1);
        this.lancamentoRepository.save(lancamento2);

        this.mockMvc.perform(MockMvcRequestBuilders
            .get(API_URL.concat("/{id}/saldo"), usuario.getId())
            .accept(JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(this.usuarioRepository, times(1)).findById(any());
        Mockito.verify(this.lancamentoRepository, times(2))
            .obterSaldoPorTipoLancamentoEUsuario(any(), any());

    }
    
    @Test
    void testSave_DeveRetornarNovoUsuario_QuandoJsonForValido() throws Exception { 

        UsuarioDto usuarioDto = UsuarioDto.builder()
            .email("usuario@gmail.com")
            .senha("senha")
        .build();

        String json = new ObjectMapper().writeValueAsString(usuarioDto);
        
        this.mockMvc.perform(MockMvcRequestBuilders
            .post(API_URL)
            .accept(JSON_VALUE)
            .contentType(JSON_VALUE)
            .content(json)
        ).andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("email").isNotEmpty());

        Mockito.verify(this.usuarioRepository, times(1)).save(any());

    }

    @Test
    void testSave_DeveRetornarException_QuandoUsuarioExistirNaBase() throws Exception { 

        Usuario usuario = UsuarioUtil.getUsuario();
        usuario = this.usuarioRepository.save(usuario);

        UsuarioDto usuarioDto = UsuarioDto.builder()
            .email(usuario.getEmail())
            .senha(usuario.getSenha())
            .build();

        String json = new ObjectMapper().writeValueAsString(usuarioDto);
        
        this.mockMvc.perform(MockMvcRequestBuilders
            .post(API_URL)
            .accept(JSON_VALUE)
            .contentType(JSON_VALUE)
            .content(json)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof RegraNegocioException))
            .andExpect(result -> Assertions.assertEquals("Já existe um usuário cadastrado com este email.", 
                result.getResolvedException().getMessage()));

        Mockito.verify(this.usuarioRepository, times(1)).save(any());
        
    }


}
