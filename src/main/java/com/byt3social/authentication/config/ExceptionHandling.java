package com.byt3social.authentication.config;

import com.byt3social.authentication.exceptions.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandling {
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity invalidTokenException(InvalidTokenException e) {
        Map<String, String> body = new HashMap<>();
        body.put("code", ((Integer) HttpStatus.UNAUTHORIZED.value()).toString());
        body.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        body.put("message", e.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }
}
