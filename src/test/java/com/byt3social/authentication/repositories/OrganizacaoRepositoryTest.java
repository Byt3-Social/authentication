package com.byt3social.authentication.repositories;

import com.byt3social.authentication.models.Organizacao;
import com.byt3social.authentication.dto.OrganizacaoDTO;
import com.byt3social.authentication.enums.StatusCadastro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Collection;
import java.util.Optional;
import java.util.Date;


import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class OrganizacaoRepositoryTest {
    @Autowired
    private OrganizacaoRepository organizacaoRepository;

    @Test
    void testFindByCnpj() {

        Organizacao organizacao = createOrganizacao("123456789", "Empresa ABC", "123456", 1);

        organizacaoRepository.save(organizacao);
        UserDetails foundOrganizacao = organizacaoRepository.findByCnpj("123456789");

        assertNotNull(foundOrganizacao);
        assertEquals("123456789", foundOrganizacao.getUsername());
    }

    @Test
    void testFindByCnpjNotFound() {

        UserDetails foundOrganizacao = organizacaoRepository.findByCnpj("non_existent_cnpj");
        assertThrows(UsernameNotFoundException.class, () -> {
            throw new UsernameNotFoundException("Organização não encontrada");
        });
        assertNull(foundOrganizacao);
    }

    @Test
    void testCreateOrganizacao() {
        Organizacao organizacao = createOrganizacao("123456789", "Empresa ABC", "123456", 1);
        Organizacao savedOrganizacao = organizacaoRepository.save(organizacao);
        assertNotNull(savedOrganizacao.getId());
    }

    @Test
    void testCreateOrganizacaoWithConstructor() {
        Organizacao organizacao = new Organizacao(null, "123456789", "Empresa ABC", "hashed_password", 1, new Date(), new Date());
        Organizacao savedOrganizacao = organizacaoRepository.save(organizacao);

        assertNotNull(savedOrganizacao.getId());
        assertEquals("123456789", savedOrganizacao.getCnpj());
        assertEquals("Empresa ABC", savedOrganizacao.getNomeEmpresarial());
    }

    @Test
    void testUpdateOrganizacao() {
        Organizacao organizacao = createOrganizacao("123456789", "Empresa ABC", "123456", 1);
        Organizacao savedOrganizacao = organizacaoRepository.save(organizacao);

        savedOrganizacao.setNomeEmpresarial("Nova Empresa XYZ");
        Organizacao updatedOrganizacao = organizacaoRepository.save(savedOrganizacao);

        assertEquals("Nova Empresa XYZ", updatedOrganizacao.getNomeEmpresarial());
    }

    @Test
    void testDeleteOrganizacao() {
        Organizacao organizacao = createOrganizacao("123456789", "Empresa ABC", "123456", 1);
        Organizacao savedOrganizacao = organizacaoRepository.save(organizacao);

        organizacaoRepository.delete(savedOrganizacao);

        Optional<Organizacao> deletedOrganizacao = organizacaoRepository.findById(savedOrganizacao.getId());

        assertTrue(deletedOrganizacao.isEmpty());
    }

    @Test
    void testFindAll() {
        Organizacao organizacao1 = createOrganizacao("111111111", "Empresa A", "111111", 2);
        Organizacao organizacao2 = createOrganizacao("222222222", "Empresa B", "222222", 3);

        organizacaoRepository.save(organizacao1);
        organizacaoRepository.save(organizacao2);

        Iterable<Organizacao> organizacoes = organizacaoRepository.findAll();

        int count = 0;
        for (Organizacao org : organizacoes) {
            count++;
        }

        assertEquals(2, count);
    }

    @Test
    void testOrganizacaoEquals() {
        Organizacao organizacao1 = createOrganizacao("123456789", "Empresa ABC", "123456", 1);
        Organizacao organizacao2 = createOrganizacao("987654321", "Outra Empresa", "123456", 2);

        assertEquals(organizacao1, organizacao2);
    }

    @Test
    void testOrganizacaoHashCode() {
        Organizacao organizacao1 = createOrganizacao("123456789", "Empresa ABC", "123456", 1);
        Organizacao organizacao2 = createOrganizacao("123456789", "Outra Empresa", "123456", 2);

        assertEquals(organizacao1.hashCode(), organizacao2.hashCode());
    }

    @Test
    void testOrganizacaoSetOrganizacaoId() {
        Organizacao organizacao = createOrganizacao("123456789", "Empresa ABC", "123456", 1);
        organizacao.setOrganizacaoId(2);

        assertEquals(2, organizacao.getOrganizacaoId());
    }

    @Test
    void testOrganizacaoSetCreatedAt() {
        Organizacao organizacao = createOrganizacao("123456789", "Empresa ABC", "123456", 1);
        Date createdAt = new Date();
        organizacao.setCreatedAt(createdAt);

        assertEquals(createdAt, organizacao.getCreatedAt());
    }

    @Test
    void testOrganizacaoSetUpdatedAt() {
        Organizacao organizacao = createOrganizacao("123456789", "Empresa ABC", "123456", 1);
        Date updatedAt = new Date();
        organizacao.setUpdatedAt(updatedAt);

        assertEquals(updatedAt, organizacao.getUpdatedAt());
    }

    @Test
    void testOrganizacaoGetAuthorities() {
        Organizacao organizacao = createOrganizacao("123456789", "Empresa ABC", "123456", 1);
        Collection<? extends GrantedAuthority> authorities = organizacao.getAuthorities();

        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZACAO")));
    }

    @Test
    void testOrganizacaoGetCnpj() {
        Organizacao organizacao = createOrganizacao("123456789", "Empresa ABC", "123456", 1);
        assertEquals("123456789", organizacao.getCnpj());
    }

    @Test
    void testOrganizacaoGetSenha() {
        String senhaPlana = "123456"; 
        Organizacao organizacao = createOrganizacao("123456789", "Empresa ABC", senhaPlana, 1);
        
        assertTrue(BCrypt.checkpw(senhaPlana, organizacao.getSenha()));
    }


    @Test
    void testOrganizacaoGetOrganizacaoId() {
        Organizacao organizacao = createOrganizacao("123456789", "Empresa ABC", "123456", 1);
        assertEquals(1, organizacao.getOrganizacaoId());
    }

    @Test
    void testOrganizacaoGetCreatedAt() {
        Organizacao organizacao = createOrganizacao("123456789", "Empresa ABC", "123456", 1);
        organizacao = organizacaoRepository.save(organizacao); 

        assertNotNull(organizacao.getCreatedAt());
    }

    @Test
    void testOrganizacaoGetUpdatedAt() {
        Organizacao organizacao = createOrganizacao("123456789", "Empresa ABC", "123456", 1);
        organizacao = organizacaoRepository.save(organizacao); 

        assertNotNull(organizacao.getUpdatedAt());
    }



    private Organizacao createOrganizacao(String cnpj, String nomeEmpresarial, String senha, Integer organizacaoId) {
        OrganizacaoDTO organizacaoDTO = new OrganizacaoDTO(cnpj, nomeEmpresarial, "org@example.com", organizacaoId, StatusCadastro.APROVADO);
        return new Organizacao(organizacaoDTO, senha);
    }

    @Test
    void testOrganizacaoGetPassword() {
        String senhaPlana = "123456";
        Organizacao organizacao = createOrganizacao("123456789", "Empresa ABC", senhaPlana, 1);

        assertTrue(BCrypt.checkpw(senhaPlana, organizacao.getPassword()));
    }
}
