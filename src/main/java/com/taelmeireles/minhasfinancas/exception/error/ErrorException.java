package com.taelmeireles.minhasfinancas.exception.error;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorException {
    
    private String message;
    private HttpStatus status;
    private Integer codeStatus;

}
