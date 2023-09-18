package com.byt3social.authentication.controllers;

import com.byt3social.authentication.models.JWTPayload;
import com.byt3social.authentication.services.TokenService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/login/oauth2")
@Getter
public class LoginController {
    @Value("${authentication.microsoft.entra-id.profile.tenant-id}")
    private String applicationID;
    @Value("${authentication.microsoft.entra-id.credentials.client-id}")
    private String clientId;
    @Value("${authentication.microsoft.entra-id.credentials.client-secret}")
    private String clientSecret;
    @Value("${authentication.microsoft.entra-id.app.scope}")
    private String scope;
    @Value("${authentication.microsoft.entra-id.app.redirect-url}")
    private String redirectUrl;

    @Autowired
    private TokenService tokenService;

    @GetMapping("")
    public ResponseEntity<Void> login() {
        String loginAPIUrl = "https://login.microsoftonline.com/" + getApplicationID() + "/oauth2/v2.0/authorize?response_type=code&response_mode=query&client_id=" + getClientId() + "&scope=" + getScope() + "&redirect_uri=" + getRedirectUrl();

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(loginAPIUrl)).build();
    }

    @GetMapping("/code")
    public ResponseEntity code(@RequestParam String code) {
        String generatedToken = tokenService.generateToken(code);

        boolean validToken = tokenService.isTokenValid(generatedToken);

        if(validToken) {
            JWTPayload jwtPayload = tokenService.getTokenClaims(generatedToken);

            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
