package com.byt3social.authentication.models;

import com.auth0.jwt.interfaces.Claim;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JWTPayload {
    private Claim name;
    private Claim email;
}
