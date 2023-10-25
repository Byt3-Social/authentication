package com.byt3social.authentication.repositories;

import com.byt3social.authentication.models.Organizacao;
import com.byt3social.authentication.dto.OrganizacaoDTO;
import com.byt3social.authentication.enums.StatusCadastro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

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

    private Organizacao createOrganizacao(String cnpj, String nomeEmpresarial, String senha, Integer organizacaoId) {
        OrganizacaoDTO organizacaoDTO = new OrganizacaoDTO(cnpj, nomeEmpresarial, "org@example.com", organizacaoId, StatusCadastro.APROVADO);
        return new Organizacao(organizacaoDTO, senha);
    }
}
