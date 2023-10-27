package com.byt3social.authentication.repositories;

import com.byt3social.authentication.models.Colaborador;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class ColaboradorRepositoryTest {
    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Test
    void testSaveColaborador() {
        Colaborador colaborador = createColaborador("Maria", "maria@example.com", "Analista");
        Colaborador savedColaborador = colaboradorRepository.save(colaborador);

        assertNotNull(savedColaborador);
        assertNotNull(savedColaborador.getId());
        assertEquals("Maria", savedColaborador.getNome());
        assertEquals("maria@example.com", savedColaborador.getEmail());
        assertEquals("Analista", savedColaborador.getFuncao());
        assertNotNull(savedColaborador.getCreatedAt());
        assertNotNull(savedColaborador.getUpdatedAt());
    }

    @Test
    void testUpdateColaborador() {
        Colaborador colaborador = createColaborador("Carlos", "carlos@example.com", "Engenheiro");
        colaboradorRepository.save(colaborador);
        colaborador.setNome("Carlos Silva");
        colaborador.setEmail("carlos.silva@example.com");
        colaborador.setFuncao("Engenheiro Sênior");
        Colaborador updatedColaborador = colaboradorRepository.save(colaborador);

        assertEquals("Carlos Silva", updatedColaborador.getNome());
        assertEquals("carlos.silva@example.com", updatedColaborador.getEmail());
        assertEquals("Engenheiro Sênior", updatedColaborador.getFuncao());
        assertNotNull(updatedColaborador.getUpdatedAt());
    }

    @Test
    void testDeleteColaborador() {
        Colaborador colaborador = createColaborador("Pedro", "pedro@example.com", "Gerente");
        colaboradorRepository.save(colaborador);
        colaboradorRepository.delete(colaborador);
        Colaborador deletedColaborador = colaboradorRepository.findByEmail("pedro@example.com");

        assertNull(deletedColaborador);
    }

    @Test
    void testFindByEmail_ExistingEmail() {
        Colaborador colaborador = createColaborador("João", "joao@example.com", "Desenvolvedor");
        colaboradorRepository.save(colaborador);
        Colaborador foundColaborador = colaboradorRepository.findByEmail("joao@example.com");

        assertNotNull(foundColaborador);
        assertEquals("João", foundColaborador.getNome());
        assertEquals("Desenvolvedor", foundColaborador.getFuncao());
        assertNotNull(foundColaborador.getCreatedAt());
        assertNotNull(foundColaborador.getUpdatedAt());
    }

    @Test
    void testFindByEmail_NonExistingEmail() {
        Colaborador foundColaborador = colaboradorRepository.findByEmail("email_que_nao_existe@example.com");
        assertNull(foundColaborador);
    }

    private Colaborador createColaborador(String nome, String email, String funcao) {
        return new Colaborador(nome, email, List.of(funcao));
    }

    @Test
    void testConstructorWithParams() {
        Colaborador colaborador = new Colaborador(1, "John Doe", "john.doe@example.com", "Developer", new Date(), new Date());
        assertNotNull(colaborador);
    }

    @Test
    void testEqualsAndHashCode() {
        Colaborador colaborador1 = createColaborador("John Doe", "john.doe@example.com", "Developer");
        Colaborador colaborador2 = createColaborador("John Doe", "john.doe@example.com", "Developer");

        assertTrue(colaborador1.equals(colaborador2));
        assertEquals(colaborador1.hashCode(), colaborador2.hashCode());
    }
}
