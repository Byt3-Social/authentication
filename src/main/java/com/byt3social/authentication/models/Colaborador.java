package com.byt3social.authentication.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Table(name = "colaboradores")
@Entity(name = "Colaborador")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Getter
@Setter
public class Colaborador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private String email;
    private String funcao;
    @CreationTimestamp
    @Column(name = "created_at")
    @JsonProperty("created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonProperty("updated_at")
    private Date updatedAt;

    public Colaborador(String nomeColaborador, String emailColaborador, List<String> funcaoColaborador) {
        this.nome = nomeColaborador;
        this.email = emailColaborador;
        this.funcao = funcaoColaborador.get(0);
    }
}
