package com.byt3social.authentication.repositories;

import com.byt3social.authentication.models.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Integer> {
    Colaborador findByEmail(String email);
}
