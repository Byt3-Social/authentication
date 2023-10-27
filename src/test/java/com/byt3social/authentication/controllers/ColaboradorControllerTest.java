package com.byt3social.authentication.controllers;

import com.byt3social.authentication.models.Colaborador;
import com.byt3social.authentication.services.ColaboradorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ColaboradorControllerTest {
    @InjectMocks
    private ColaboradorController colaboradorController;

    @Mock
    private ColaboradorService colaboradorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_ValidToken() {
        String validToken = "validToken";
        Mockito.when(colaboradorService.login(validToken)).thenReturn(true);

        ResponseEntity<Void> response = colaboradorController.login("Bearer " + validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testLogin_InvalidToken() {
        String invalidToken = "invalidToken";
        Mockito.when(colaboradorService.login(invalidToken)).thenReturn(false);

        ResponseEntity<Void> response = colaboradorController.login("Bearer " + invalidToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testValidarToken_ValidToken() {
        String validToken = "validToken";
        Colaborador colaborador = createColaborador();
        Mockito.when(colaboradorService.validarTokenJWT(validToken)).thenReturn(true);
        Mockito.when(colaboradorService.buscarColaborador(validToken)).thenReturn(colaborador);

        ResponseEntity<Colaborador> response = colaboradorController.validarToken("Bearer " + validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(colaborador, response.getBody());
    }

    @Test
    void testValidarToken_InvalidToken() {
        String invalidToken = "invalidToken";
        Mockito.when(colaboradorService.validarTokenJWT(invalidToken)).thenReturn(false);

        ResponseEntity response = colaboradorController.validarToken("Bearer " + invalidToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testConsultarColaborador() {
        int colaboradorId = 1;
        Colaborador colaborador = createColaborador();
        Mockito.when(colaboradorService.buscarColaboradorPorId(colaboradorId)).thenReturn(colaborador);

        ResponseEntity<Colaborador> response = colaboradorController.consultarColaborador(colaboradorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(colaborador, response.getBody());
    }

    private Colaborador createColaborador() {
        Colaborador colaborador = new Colaborador();
        colaborador.setId(1);
        colaborador.setNome("John Doe");
        colaborador.setEmail("john.doe@example.com");
        colaborador.setFuncao("Developer");
        return colaborador;
    }
}
