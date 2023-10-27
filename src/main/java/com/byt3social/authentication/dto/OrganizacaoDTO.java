package com.byt3social.authentication.dto;

import com.byt3social.authentication.enums.StatusCadastro;
import com.fasterxml.jackson.annotation.JsonProperty;

public record OrganizacaoDTO(
        String cnpj,
        String nomeEmpresarial,
        String email,
        @JsonProperty("id")
        Integer organizacaoId,
        StatusCadastro statusCadastro
) {
}
