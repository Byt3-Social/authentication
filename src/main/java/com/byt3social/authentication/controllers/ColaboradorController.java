package com.byt3social.authentication.controllers;

import com.byt3social.authentication.models.Colaborador;
import com.byt3social.authentication.services.ColaboradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Colaborador Controller", description = "API para operações relacionadas a colaboradores")
public class ColaboradorController {
    @Autowired
    private ColaboradorService colaboradorService;

    @Operation(summary = "Autenticar um colaborador")
    @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida")
    @ApiResponse(responseCode = "401", description = "Autenticação mal-sucedida")
    @PostMapping("/colaborador/login")
    public ResponseEntity<Void> login(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");

        if (!colaboradorService.login(token)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "Validar o token de um colaborador")
    @ApiResponse(responseCode = "200", description = "Token válido", content = @Content(schema = @Schema(implementation = Colaborador.class)))
    @ApiResponse(responseCode = "401", description = "Token inválido")
    @PostMapping("/colaborador/validar")
    public ResponseEntity validarToken(@RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(description = "Token JWT no formato 'Bearer <token>'", example = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Boolean tokenValido = colaboradorService.validarTokenJWT(token);

        if (!tokenValido) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        Colaborador colaborador = colaboradorService.buscarColaborador(token);

        return new ResponseEntity<>(colaborador, HttpStatus.OK);
    }

    @Operation(summary = "Consultar um colaborador por ID")
    @ApiResponse(responseCode = "200", description = "Colaborador encontrado", content = @Content(schema = @Schema(implementation = Colaborador.class)))
    @ApiResponse(responseCode = "404", description = "Colaborador não encontrado")
    @GetMapping("/colaborador/{id}")
    public ResponseEntity consultarColaborador(@PathVariable("id") Integer colaboradorId) {
        Colaborador colaborador = colaboradorService.buscarColaboradorPorId(colaboradorId);

        if (colaborador == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(colaborador, HttpStatus.OK);
    }
}
