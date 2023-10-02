package com.taelmeireles.minhasfinancas.service.impl;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taelmeireles.minhasfinancas.exception.AutenticacaoException;
import com.taelmeireles.minhasfinancas.model.Usuario;
import com.taelmeireles.minhasfinancas.service.TokenJwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenJwtServiceImpl implements TokenJwtService {

    @Value("${token.jwt.expiracao}")
    private String expiracao;

    @Value("${token.jwt.chave-assinatura}")
    private String chaveAssinatura;

    @Override
    public String gerarToken(Usuario usuario) {
        int dataExpiracao = Integer.parseInt(expiracao);
        
        return Jwts.builder()
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * dataExpiracao))
            .setSubject(usuario.getEmail())
            .signWith(getKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    @Override
    public Boolean isTokenValido(String token) {
        Claims claims = obterClaims(token);
        Date dataExpiracao = claims.getExpiration();
        return dataExpiracao.after(new Date());
    }
    
    @Override
    public String obterLoginUsuario(String token) {
        Claims claims = obterClaims(token);
        return claims.getSubject();
    }
    
    private Claims obterClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new AutenticacaoException("Token inválido");
        }
    }
    
    private Key getKey() {
        byte[] keyDecode = Decoders.BASE64.decode(chaveAssinatura);
        return Keys.hmacShaKeyFor(keyDecode);
    }


}
