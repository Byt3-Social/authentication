package com.byt3social.authentication.dto;

import com.byt3social.authentication.enums.StatusCadastro;
import com.fasterxml.jackson.annotation.JsonProperty;

public record OrganizacaoDTO(
        String cnpj,
        @JsonProperty("nome_empresarial")
        String nomeEmpresarial,
        String email,
        @JsonProperty("id")
        Integer organizacaoId,
        @JsonProperty("status_cadastro")
        StatusCadastro statusCadastro
) {
}
