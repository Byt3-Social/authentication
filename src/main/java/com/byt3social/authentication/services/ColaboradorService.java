package com.byt3social.authentication.services;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.byt3social.authentication.models.Colaborador;
import com.byt3social.authentication.repositories.ColaboradorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
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

    public boolean login(String token) {
        if(!validarTokenJWT(token)) {
            return false;
        }

        Colaborador colaborador = buscarColaborador(token);

        if(colaborador == null) {
            cadastrarColaborador(token);
        }

        return true;
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

            return !isJWTExpired(decodedJWT);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isJWTExpired(DecodedJWT decodedJWT) {
        Date expiresAt = decodedJWT.getExpiresAt();
        return expiresAt.before(new Date());
    }

    public Colaborador buscarColaborador(String token) {
        DecodedJWT tokenDecodificado = JWT.decode(token);
        Colaborador colaborador = colaboradorRepository.findByEmail(tokenDecodificado.getClaim("unique_name").asString());
        return colaborador;
    }

    @Transactional
    public void cadastrarColaborador(String token) {
        DecodedJWT tokenDecodificado = JWT.decode(token);
        String nomeColaborador = tokenDecodificado.getClaim("name").asString();
        String emailColaborador = tokenDecodificado.getClaim("unique_name").asString();
        List<String> funcaoColaborador = tokenDecodificado.getClaim("roles").asList(String.class);
        Colaborador colaborador = new Colaborador(nomeColaborador, emailColaborador, funcaoColaborador);

        colaboradorRepository.save(colaborador);
    }

    public Colaborador buscarColaboradorPorId(Integer colaboradorId) {
        return colaboradorRepository.findById(colaboradorId).get();
    }
}
