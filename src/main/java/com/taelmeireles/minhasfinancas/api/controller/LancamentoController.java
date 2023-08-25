package com.taelmeireles.minhasfinancas.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taelmeireles.minhasfinancas.dto.LancamentoDto;
import com.taelmeireles.minhasfinancas.service.LancamentoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {
    
    private final LancamentoService service;

    @GetMapping
    public ResponseEntity<?> buscar(@RequestParam(value = "descricao", required = false) String descricao, 
                                    @RequestParam(value = "mes", required = false) Integer mes, 
                                    @RequestParam(value = "ano", required = false) Integer ano, 
                                    @RequestParam(value = "usuarioId", required = true) UUID usuarioId) {
        
        LancamentoDto lancamentoDto = LancamentoDto.builder().build();
        lancamentoDto.setDescricao(descricao);
        lancamentoDto.setAno(ano);
        lancamentoDto.setMes(mes);
        lancamentoDto.setUsuarioId(usuarioId);

        List<LancamentoDto> lancamentoDtoList = this.service.buscar(lancamentoDto);      
        return ResponseEntity.status(HttpStatus.OK).body(lancamentoDtoList);
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody LancamentoDto dto) {
        LancamentoDto lancamentoDto = this.service.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoDto);
    }
    
    @PutMapping
    public ResponseEntity<?> atualizar(@RequestBody LancamentoDto dto) {
        LancamentoDto lancamentoDto = this.service.atualizar(dto);
        return ResponseEntity.status(HttpStatus.OK).body(lancamentoDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarStatus(@PathVariable("id") UUID id, @RequestParam("status") String status) {
        this.service.atualizarStatus(id, status);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") UUID lancamentoId) {
        this.service.deletar(lancamentoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }




}
