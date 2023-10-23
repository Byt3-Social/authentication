package com.byt3social.authentication.controllers;

import com.byt3social.authentication.dto.OrganizacaoLoginDTO;
import com.byt3social.authentication.models.Organizacao;
import com.byt3social.authentication.models.OrganizacaoToken;
import com.byt3social.authentication.services.OrganizacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrganizacaoController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private OrganizacaoService organizacaoService;

    @PostMapping("/organizacao/login")
    public ResponseEntity login(@RequestBody OrganizacaoLoginDTO organizacaoLoginDTO) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(organizacaoLoginDTO.usuario(), organizacaoLoginDTO.senha());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

       OrganizacaoToken token = new OrganizacaoToken(organizacaoService.gerarTokenJWT((Organizacao) authentication.getPrincipal()));

       if(token == null) {
           return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
       }

        return new ResponseEntity(token, HttpStatus.OK);
    }

    @PostMapping("/organizacao/validar")
    public ResponseEntity validarToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        String token = authorization.replace("Bearer ", "");
        Boolean tokenValido = organizacaoService.validarTokenJWT(token);

        if(!tokenValido) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        Organizacao organizacao = organizacaoService.buscarOrganizacao(token);

        return new ResponseEntity<>(organizacao, HttpStatus.OK);
    }
}
