package com.taelmeireles.minhasfinancas.exception.handle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.taelmeireles.minhasfinancas.exception.AutenticacaoException;
import com.taelmeireles.minhasfinancas.exception.RegraNegocioException;
import com.taelmeireles.minhasfinancas.exception.error.ErrorException;

@RestControllerAdvice
public class HandleException {
    
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErrorException> regraNegocioException(RegraNegocioException exception) {
        
        ErrorException error = ErrorException.builder()
            .message(exception.getMessage())
            .status(HttpStatus.BAD_REQUEST)
            .codeStatus(HttpStatus.BAD_REQUEST.value())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }
    
    @ExceptionHandler(AutenticacaoException.class)
    public ResponseEntity<ErrorException> autenticacaoException(AutenticacaoException exception) {
        
        ErrorException error = ErrorException.builder()
            .message(exception.getMessage())
            .status(HttpStatus.BAD_REQUEST)
            .codeStatus(HttpStatus.BAD_REQUEST.value())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    

}
