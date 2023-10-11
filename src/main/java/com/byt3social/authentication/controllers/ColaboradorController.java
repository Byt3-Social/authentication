package com.byt3social.authentication.controllers;

import com.byt3social.authentication.models.Colaborador;
import com.byt3social.authentication.services.ColaboradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class ColaboradorController {
    @Autowired
    private ColaboradorService colaboradorService;

    @GetMapping("/colaborador/login")
    public ResponseEntity<Void> login() {
        String loginUrl = colaboradorService.recuperarUrlLogin();

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(loginUrl)).build();
    }

    @GetMapping(value = "/colaborador/code")
    public ResponseEntity code(@RequestParam String code) {
        String tokenGerado = colaboradorService.gerarTokenJWT(code);

        return new ResponseEntity(tokenGerado, HttpStatus.OK);
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
}
