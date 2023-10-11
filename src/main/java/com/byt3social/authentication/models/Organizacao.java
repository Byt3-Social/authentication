package com.byt3social.authentication.models;

import com.byt3social.authentication.dto.OrganizacaoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Table(name = "organizacoes")
@Entity(name = "Organizacao")
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@JsonIgnoreProperties(value = {"senha", "password"})
@Getter
@Setter
public class Organizacao implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String cnpj;
    @Column(name = "nome_empresarial")
    private String nomeEmpresarial;
    private String senha;
    @Column(name = "organizacao_id")
    private Integer organizacaoId;
    @CreationTimestamp
    @Column(name = "created_at")
    @JsonProperty("created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonProperty("updated_at")
    private Date updatedAt;

    public Organizacao(OrganizacaoDTO organizacaoDTO, String senha) {
        this.nomeEmpresarial = organizacaoDTO.nomeEmpresarial();
        this.cnpj = organizacaoDTO.cnpj();
        this.senha = BCrypt.hashpw(senha, BCrypt.gensalt());
        this.organizacaoId = organizacaoDTO.organizacaoId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ORGANIZACAO"));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.cnpj;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
