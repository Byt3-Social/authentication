package com.byt3social.authentication.repositories;

import com.byt3social.authentication.models.Organizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface OrganizacaoRepository extends JpaRepository<Organizacao, Integer> {
    UserDetails findByCnpj(String cnpj);
}
