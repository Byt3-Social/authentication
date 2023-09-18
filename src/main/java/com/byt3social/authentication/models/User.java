package com.byt3social.authentication.models;

import com.byt3social.authentication.enums.Role;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Table(name = "users")
@Entity(name = "User")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Date last_login;

    public User(JWTPayload jwtPayload) {
        this.name = jwtPayload.getName().asString();
        this.email = jwtPayload.getEmail().asString();
        this.role = Role.ADMIN;
        this.last_login = Date.from(Instant.now());
    }

    public void updateLastLogin() {
        this.last_login = Date.from(Instant.now());
    }
}
