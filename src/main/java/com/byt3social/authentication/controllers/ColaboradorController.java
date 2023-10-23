package com.byt3social.authentication.controllers;

import com.byt3social.authentication.models.Colaborador;
import com.byt3social.authentication.services.ColaboradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ColaboradorController {
    @Autowired
    private ColaboradorService colaboradorService;

    @PostMapping("/colaborador/login")
    public ResponseEntity<Void> login(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");

        if(!colaboradorService.login(token)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/colaborador/validar")
    public ResponseEntity validarToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        String token = authorization.replace("Bearer ", "");
        Boolean tokenValido = colaboradorService.validarTokenJWT(token);

        if(!tokenValido) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        Colaborador colaborador = colaboradorService.buscarColaborador(token);

        return new ResponseEntity<>(colaborador, HttpStatus.OK);
    }

    @GetMapping("/colaborador/{id}")
    public ResponseEntity consultarColaborador(@PathVariable("id") Integer colaboradorId) {
        Colaborador colaborador = colaboradorService.buscarColaboradorPorId(colaboradorId);

        return new ResponseEntity(colaborador, HttpStatus.OK);
    }
}
