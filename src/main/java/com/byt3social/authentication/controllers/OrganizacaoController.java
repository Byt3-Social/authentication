package com.byt3social.authentication.controllers;

import com.byt3social.authentication.dto.OrganizacaoLoginDTO;
import com.byt3social.authentication.models.Organizacao;
import com.byt3social.authentication.models.OrganizacaoToken;
import com.byt3social.authentication.services.OrganizacaoService;

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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/organizacao")
@Tag(name = "Organizacao Controller", description = "API para operações relacionadas a organizações")
public class OrganizacaoController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private OrganizacaoService organizacaoService;

    @Operation(summary = "Autenticar uma organização")
    @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida", content = @Content(schema = @Schema(implementation = OrganizacaoToken.class)))
    @ApiResponse(responseCode = "401", description = "Autenticação mal-sucedida")
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody OrganizacaoLoginDTO organizacaoLoginDTO) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(organizacaoLoginDTO.usuario(), organizacaoLoginDTO.senha());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        OrganizacaoToken token = new OrganizacaoToken(organizacaoService.gerarTokenJWT((Organizacao) authentication.getPrincipal()));

        return new ResponseEntity(token, HttpStatus.OK);
    }

    @Operation(summary = "Validar o token de uma organização")
    @ApiResponse(responseCode = "200", description = "Token válido", content = @Content(schema = @Schema(implementation = Organizacao.class)))
    @ApiResponse(responseCode = "401", description = "Token inválido")
    @PostMapping("/validar")
    public ResponseEntity validarToken(@RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(description = "Token JWT no formato 'Bearer <token>'", example = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Boolean tokenValido = organizacaoService.validarTokenJWT(token);

        if(!tokenValido) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        Organizacao organizacao = organizacaoService.buscarOrganizacao(token);

        return new ResponseEntity<>(organizacao, HttpStatus.OK);
    }
}
