package com.byt3social.authentication.services;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.byt3social.authentication.dto.AccessTokenDTO;
import com.byt3social.authentication.models.JWTPayload;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;

@Component
@Getter
public class TokenService {
    @Value("${authentication.microsoft.entra-id.profile.tenant-id}")
    private String applicationID;
    @Value("${authentication.microsoft.entra-id.credentials.client-id}")
    private String clientId;
    @Value("${authentication.microsoft.entra-id.credentials.client-secret}")
    private String clientSecret;
    @Value("${authentication.microsoft.entra-id.app.scope}")
    private String scope;

    public String generateToken(String code) {
        String tokenAPIUrl = "https://login.microsoftonline.com/" + getApplicationID() + "/oauth2/v2.0/token";

        RestTemplate tokenAPI = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<String, String>();
        data.add("client_id", getClientId());
        data.add("scope", getScope());
        data.add("code", code);
        data.add("redirect_uri", "http://localhost:8080/login/oauth2/code");
        data.add("grant_type", "authorization_code");
        data.add("client_secret", getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(data, headers);

        ResponseEntity<AccessTokenDTO> response = tokenAPI.postForEntity(tokenAPIUrl, request, AccessTokenDTO.class);

        AccessTokenDTO accessTokenDTO = response.getBody();

        return accessTokenDTO.access_token();
    }

    public boolean isTokenValid(String token) {
        String keysUrl = "https://login.microsoftonline.com/" + getApplicationID() + "/discovery/keys?appid=" + getClientId();

        DecodedJWT decodedJWT = JWT.decode(token);

        try {
            JwkProvider provider = new UrlJwkProvider(new URL(keysUrl));
            Jwk jwk = provider.get(decodedJWT.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withAudience("api://" + getClientId())
                    .build();
            decodedJWT = verifier.verify(token);
        } catch (JWTVerificationException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return true;
    }

    public JWTPayload getTokenClaims(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);

        return new JWTPayload(decodedJWT.getClaim("name"), decodedJWT.getClaim("unique_name"));
    }
}
