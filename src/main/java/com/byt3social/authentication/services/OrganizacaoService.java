package com.byt3social.authentication.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.byt3social.authentication.dto.OrganizacaoDTO;
import com.byt3social.authentication.models.Organizacao;
import com.byt3social.authentication.repositories.OrganizacaoRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class OrganizacaoService implements UserDetailsService {
    @Value("${com.byt3social.token.algorithm.secret-key}")
    private String secretKey;
    @Autowired
    private OrganizacaoRepository organizacaoRepository;
    @Autowired
    private EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String cnpj) throws UsernameNotFoundException {
        return organizacaoRepository.findByCnpj(cnpj);
    }

    public String gerarTokenJWT(Organizacao organizacao) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            return JWT.create()
                    .withAudience("social.b3.com.br")
                    .withIssuer("B3")
                    .withSubject(organizacao.getNomeEmpresarial())
                    .withClaim("cnpj", organizacao.getCnpj())
                    .withClaim("roles", List.of("B3Social.Organizacao"))
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(recuperarDataExpiracao())
                    .sign(algorithm);
        } catch(Exception e) {
            return null;
        }
    }

    public Boolean validarTokenJWT(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withAudience("social.b3.com.br")
                    .withIssuer("B3")
                    .build();

            verifier.verify(token);

            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public Organizacao buscarOrganizacao(String token) {
        DecodedJWT tokenDecodificado = JWT.decode(token);
        Organizacao organizacao = (Organizacao) organizacaoRepository.findByCnpj(tokenDecodificado.getClaim("cnpj").asString());

        return organizacao;
    }

    private Instant recuperarDataExpiracao() {
        return LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.of("-04:00"));
    }

    @Transactional
    public void cadastrarUsuario(OrganizacaoDTO organizacaoDTO) {
        String senha = RandomStringUtils.randomAlphanumeric(10, 11);
        Organizacao organizacao = new Organizacao(organizacaoDTO, senha);

        organizacaoRepository.save(organizacao);

        emailService.notificarOrganizacao(organizacaoDTO, senha);
    }
}
