package com.taelmeireles.minhasfinancas.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taelmeireles.minhasfinancas.dto.LancamentoDto;
import com.taelmeireles.minhasfinancas.enums.StatusLancamento;
import com.taelmeireles.minhasfinancas.enums.TipoLancamento;
import com.taelmeireles.minhasfinancas.model.Usuario;
import com.taelmeireles.minhasfinancas.service.LancamentoService;

@WebMvcTest(LancamentoController.class)
@ActiveProfiles("test")
class LancamentoControllerTest {
    
    private static final String API_URL = "/api/lancamentos";
    private static final MediaType JSON_VALUE = MediaType.APPLICATION_JSON;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LancamentoService lancamentoService;
    

    @Test
    void testAtualizar() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());

        LancamentoDto lancamentoDto = LancamentoDto.builder()
            .id(UUID.randomUUID())
            .descricao("teste")
            .mes(1)
            .ano(2023)
            .valor(BigDecimal.valueOf(500))
            .tipo(TipoLancamento.RECEITA)
            .status(StatusLancamento.PENDENTE)
            .usuarioId(usuario.getId())
            .build();
        
        Mockito.when(this.lancamentoService.atualizar(any(LancamentoDto.class))).thenReturn(lancamentoDto);

        String json = new ObjectMapper().writeValueAsString(lancamentoDto);

        this.mockMvc.perform(MockMvcRequestBuilders.put(API_URL)
            .accept(JSON_VALUE)
            .contentType(JSON_VALUE)
            .content(json)
        ).andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("usuarioId").isNotEmpty());

        Mockito.verify(this.lancamentoService, times(1)).atualizar(any(LancamentoDto.class));
    }


    @Test
    void testAtualizarStatus() throws Exception {

        Mockito.doNothing().when(this.lancamentoService).atualizarStatus(any(UUID.class), anyString());

        this.mockMvc.perform(MockMvcRequestBuilders.patch(API_URL.concat("/{id}"), UUID.randomUUID())
            .accept(JSON_VALUE)
            .contentType(JSON_VALUE)
            .param("status", StatusLancamento.EFETIVADO.name())
        ).andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(this.lancamentoService, times(1))
            .atualizarStatus(any(UUID.class), anyString());

    }

    @Test
    void testBuscar() throws Exception {
        
        Mockito.when(this.lancamentoService.buscar(any(LancamentoDto.class)))
            .thenReturn(List.of());
        
        this.mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
            .accept(JSON_VALUE)
            .contentType(JSON_VALUE)
            .param("descricao", "teste")
            .param("usuarioId", UUID.randomUUID().toString())
        ).andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(result -> Assertions.assertEquals(0, result.getResponse().getContentLength()));

        Mockito.verify(this.lancamentoService, times(1))
            .buscar(any(LancamentoDto.class));

    }

    @Test
    void testDeletar() throws Exception {

        Mockito.doNothing().when(this.lancamentoService).deletar(any(UUID.class));

        this.mockMvc.perform(MockMvcRequestBuilders.delete(API_URL.concat("/{id}"), UUID.randomUUID())
            .accept(JSON_VALUE)
            .contentType(JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(this.lancamentoService, times(1))
            .deletar(any(UUID.class));

    }

    @Test
    void testSalvar() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());

        LancamentoDto lancamentoDto = LancamentoDto.builder()
            .id(UUID.randomUUID())
            .descricao("teste")
            .mes(1)
            .ano(2023)
            .valor(BigDecimal.valueOf(500))
            .tipo(TipoLancamento.RECEITA)
            .status(StatusLancamento.PENDENTE)
            .usuarioId(usuario.getId())
            .build();

        String json = new ObjectMapper().writeValueAsString(lancamentoDto);

        Mockito.when(this.lancamentoService.salvar(any(LancamentoDto.class))).thenReturn(lancamentoDto);

        this.mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
            .accept(JSON_VALUE)
            .contentType(JSON_VALUE)
            .content(json)
        ).andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("usuarioId").isNotEmpty());
        

        Mockito.verify(this.lancamentoService, times(1))
            .salvar(any(LancamentoDto.class));

    }
    
}
