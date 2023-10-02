package com.taelmeireles.minhasfinancas.config;

import java.io.IOException;
import java.util.Objects;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import com.taelmeireles.minhasfinancas.service.TokenJwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TokenJwtFilterConfig extends OncePerRequestFilter {
    
    private final TokenJwtService tokenJwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authorization = request.getHeader("Authorization");
        
        if(Objects.isNull(authorization) || !authorization.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = authorization.split(" ")[1];

        if(Objects.nonNull(token)) {
            String login = this.tokenJwtService.validTokenReturnSubject(token);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(login);
            if(Objects.nonNull(userDetails)) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    

}
