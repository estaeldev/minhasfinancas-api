package com.taelmeireles.minhasfinancas.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
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
    public String validTokenReturnSubject(String token) {
        try {
            Algorithm algorithm = getAlgorithm();
            DecodedJWT decodeJwt = JWT.require(algorithm).build().verify(token);
            return decodeJwt.getSubject();
        } catch (Exception e) {
            return "";
        }
    }
    
    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(chaveAssinatura);
    }

}
