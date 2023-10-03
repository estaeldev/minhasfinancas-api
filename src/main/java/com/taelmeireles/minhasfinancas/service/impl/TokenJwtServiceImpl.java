package com.taelmeireles.minhasfinancas.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.taelmeireles.minhasfinancas.exception.RegraNegocioException;
import com.taelmeireles.minhasfinancas.service.TokenJwtService;

@Service
public class TokenJwtServiceImpl implements TokenJwtService {

    @Value("${token.jwt.expiracao}")
    private String expiracao;

    @Value("${token.jwt.chave-assinatura}")
    private String chaveAssinatura;
    
    @Override
    public String gerarToken(String email) {
        try {
            int dataExpiracao = Integer.parseInt(expiracao);
            Algorithm algorithm = getAlgorithm();
    
            return JWT.create()
                .withIssuer("Minhas Financas Api")
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * dataExpiracao))
                .sign(algorithm);
        } catch (Exception e) {
            throw new RegraNegocioException("Erro ao gerar o token");
        }
    }
    
    @Override
    public String obterLoginUsuario(String token) {
        return JWT.decode(token).getSubject();
    }
    
    @Override
    public Boolean validarToken(String token) {
        try {
            Algorithm algorithm = getAlgorithm();
            JWT.require(algorithm).build().verify(token);
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(chaveAssinatura);
    }

}
