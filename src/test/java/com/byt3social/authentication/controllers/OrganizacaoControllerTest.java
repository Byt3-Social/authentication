package com.byt3social.authentication.controllers;

import com.byt3social.authentication.dto.OrganizacaoLoginDTO;
import com.byt3social.authentication.models.Organizacao;
import com.byt3social.authentication.models.OrganizacaoToken;
import com.byt3social.authentication.services.OrganizacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class OrganizacaoControllerTest {
    @InjectMocks
    private OrganizacaoController organizacaoController;

    @Mock
    private OrganizacaoService organizacaoService;

    @Mock
    private AuthenticationManager authenticationManager;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_InvalidCredentials() {
        OrganizacaoLoginDTO loginDTO = new OrganizacaoLoginDTO("invalid_user", "invalid_password");

        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        try {
            ResponseEntity response = organizacaoController.login(loginDTO);
            fail("Expected BadCredentialsException was not thrown");
        } catch (BadCredentialsException ex) {
            assertEquals("Invalid credentials", ex.getMessage());
        }
    }

    @Test
    void testLogin_ValidCredentials() {
        OrganizacaoLoginDTO loginDTO = new OrganizacaoLoginDTO("username", "password");
        Organizacao organizacao = createOrganizacao();
        OrganizacaoToken token = new OrganizacaoToken("valid_token");

        Authentication authentication = new UsernamePasswordAuthenticationToken(organizacao, null);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);

        Mockito.when(organizacaoService.gerarTokenJWT(organizacao)).thenReturn("valid_token");
        Mockito.when(organizacaoService.loadUserByUsername("username")).thenReturn(organizacao);

        ResponseEntity response = organizacaoController.login(loginDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void testValidarToken_ValidToken() {
        String validToken = "validToken";
        Organizacao organizacao = createOrganizacao();

        Mockito.when(organizacaoService.validarTokenJWT(validToken)).thenReturn(true);
        Mockito.when(organizacaoService.buscarOrganizacao(validToken)).thenReturn(organizacao);

        ResponseEntity response = organizacaoController.validarToken("Bearer " + validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(organizacao, response.getBody());
    }

    @Test
    void testValidarToken_InvalidToken() {
        String invalidToken = "invalidToken";
        Mockito.when(organizacaoService.validarTokenJWT(invalidToken)).thenReturn(false);

        ResponseEntity response = organizacaoController.validarToken("Bearer " + invalidToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    private Organizacao createOrganizacao() {
        Organizacao organizacao = new Organizacao();
        organizacao.setId(1);
        organizacao.setCnpj("1234567890");
        organizacao.setNomeEmpresarial("Organization");
        organizacao.setSenha("hashed_password");
        return organizacao;
    }
}
