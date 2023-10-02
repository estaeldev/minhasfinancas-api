package com.taelmeireles.minhasfinancas.api.controller;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taelmeireles.minhasfinancas.dto.TokenJwtDto;
import com.taelmeireles.minhasfinancas.dto.UsuarioDto;
import com.taelmeireles.minhasfinancas.mapper.UsuarioMapper;
import com.taelmeireles.minhasfinancas.model.Usuario;
import com.taelmeireles.minhasfinancas.service.LancamentoService;
import com.taelmeireles.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    private final LancamentoService lancamentoService;
    
    @PostMapping
    public ResponseEntity<?> save(@RequestBody UsuarioDto dto) {
        Usuario usuario = UsuarioMapper.fromDtoToEntity(dto);
        Usuario usuarioSaved = this.usuarioService.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSaved);
    }
    
    @PostMapping("/auth")
    public ResponseEntity<?> autenticar(@RequestBody UsuarioDto dto) {
        TokenJwtDto usuarioAutenticado = this.usuarioService.authenticate(dto.getEmail(), dto.getSenha());
        return ResponseEntity.status(HttpStatus.OK).body(usuarioAutenticado);
    }
    
    @GetMapping("/{id}/saldo")
    public ResponseEntity<?> obterSaldo(@PathVariable("id") UUID id) {
        Usuario usuario = this.usuarioService.findById(id);
        
        BigDecimal saldo = this.lancamentoService.obterSaldoPorUsuario(usuario.getId());
        return ResponseEntity.status(HttpStatus.OK).body(saldo);
    }   

}
