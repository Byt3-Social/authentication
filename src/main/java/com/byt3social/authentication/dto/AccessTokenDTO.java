package com.byt3social.authentication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessTokenDTO(
        @JsonProperty("access_token")
        String accessToken
) {
}
