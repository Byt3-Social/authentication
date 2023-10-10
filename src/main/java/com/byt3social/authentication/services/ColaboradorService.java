package com.byt3social.authentication.services;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.byt3social.authentication.dto.AccessTokenDTO;
import com.byt3social.authentication.models.Colaborador;
import com.byt3social.authentication.repositories.ColaboradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Service
public class ColaboradorService {
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
    @Value("${authentication.microsoft.entra-id.app.redirect-url-encoded}")
    private String redirectUrlEncoded;
    @Value("${authentication.microsoft.entra-id.app.api-app-id}")
    private String aud;
    @Autowired
    private ColaboradorRepository colaboradorRepository;

    public String recuperarUrlLogin() {
        String loginUrl = "https://login.microsoftonline.com/" + applicationID + "/oauth2/v2.0/authorize?response_type=code&response_mode=query&client_id=" + clientId + "&scope=" + scope + "&redirect_uri=" + redirectUrlEncoded;

        return loginUrl;
    }

    public String gerarTokenJWT(String code) {
        String tokenEndpoint = "https://login.microsoftonline.com/" + applicationID + "/oauth2/v2.0/token";

        RestTemplate tokenRequest = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<String, String>();
        data.add("client_id", clientId);
        data.add("scope", scope);
        data.add("code", code);
        data.add("redirect_uri", redirectUrl);
        data.add("grant_type", "authorization_code");
        data.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(data, headers);

        AccessTokenDTO accessTokenDTO = tokenRequest.postForObject(tokenEndpoint, request, AccessTokenDTO.class);

        String token = accessTokenDTO.accessToken();

        DecodedJWT tokenDecodificado = JWT.decode(token);
        Colaborador colaborador = colaboradorRepository.findByEmail(tokenDecodificado.getClaim("unique_name").asString());

        if(colaborador == null) {
            cadastrarColaborador(token);
        }

        return token;
    }

    public boolean validarTokenJWT(String token) {
        String keysUrl = "https://login.microsoftonline.com/" + applicationID + "/discovery/keys?appid=" + clientId;

        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            JwkProvider provider = new UrlJwkProvider(new URL(keysUrl));
            Jwk jwk = provider.get(decodedJWT.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withAudience("api://" + aud)
                    .build();
            decodedJWT = verifier.verify(token);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private void cadastrarColaborador(String token) {
        DecodedJWT tokenDecodificado = JWT.decode(token);
        String nomeColaborador = tokenDecodificado.getClaim("name").asString();
        String emailColaborador = tokenDecodificado.getClaim("unique_name").asString();
        List<String> funcaoColaborador = tokenDecodificado.getClaim("roles").asList(String.class);
        Colaborador colaborador = new Colaborador(nomeColaborador, emailColaborador, funcaoColaborador);

        colaboradorRepository.save(colaborador);
    }
}
