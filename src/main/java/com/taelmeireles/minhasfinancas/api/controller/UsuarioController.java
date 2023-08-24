package com.taelmeireles.minhasfinancas.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taelmeireles.minhasfinancas.dto.UsuarioDto;
import com.taelmeireles.minhasfinancas.mapper.UsuarioMapper;
import com.taelmeireles.minhasfinancas.model.Usuario;
import com.taelmeireles.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    @PostMapping
    public ResponseEntity<?> save(@RequestBody UsuarioDto dto) {
        Usuario usuario = UsuarioMapper.fromDtoToEntity(dto);
        Usuario usuarioSaved = this.usuarioService.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSaved);
    }
    
    @PostMapping("/auth")
    public ResponseEntity<?> autenticar(@RequestBody UsuarioDto dto) {
        Usuario usuario = UsuarioMapper.fromDtoToEntity(dto);
        Usuario usuarioAutenticado = this.usuarioService.authenticate(usuario.getEmail(), usuario.getSenha());
        return ResponseEntity.status(HttpStatus.OK).body(usuarioAutenticado);
    }
    


}
