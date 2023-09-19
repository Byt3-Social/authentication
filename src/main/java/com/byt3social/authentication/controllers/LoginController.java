package com.byt3social.authentication.controllers;

import com.byt3social.authentication.exceptions.InvalidTokenException;
import com.byt3social.authentication.models.JWTPayload;
import com.byt3social.authentication.models.User;
import com.byt3social.authentication.repositories.UserRepository;
import com.byt3social.authentication.services.TokenService;
import com.byt3social.authentication.services.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
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
    @Value("${authentication.microsoft.entra-id.app.redirect-url-encoded}")
    private String redirectUrlEncoded;

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        String loginAPIUrl = "https://login.microsoftonline.com/" + getApplicationID() + "/oauth2/v2.0/authorize?response_type=code&response_mode=query&client_id=" + getClientId() + "&scope=" + getScope() + "&redirect_uri=" + getRedirectUrlEncoded();

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(loginAPIUrl)).build();
    }

    @GetMapping(value = "/code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity code(@RequestParam String code) {
        String generatedToken = tokenService.generateToken(code);

        boolean validToken = tokenService.isTokenValid(generatedToken);

        if(validToken) {
            JWTPayload jwtPayload = tokenService.getTokenClaims(generatedToken);

            User user = userRepository.findByEmail(jwtPayload.getEmail().asString());

            if(user != null) {
                user = userService.updateUserLastLogin(user);
            } else {
                user = userService.registerUser(jwtPayload);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("token", generatedToken);
            response.put("user", user);

            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        } else {
            throw new InvalidTokenException();
        }
    }
}
