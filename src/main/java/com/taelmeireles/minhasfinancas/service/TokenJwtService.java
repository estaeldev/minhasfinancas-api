package com.taelmeireles.minhasfinancas.service;

public interface TokenJwtService {
    
    String gerarToken(String email);

    String validTokenReturnSubject(String token);
    
}
