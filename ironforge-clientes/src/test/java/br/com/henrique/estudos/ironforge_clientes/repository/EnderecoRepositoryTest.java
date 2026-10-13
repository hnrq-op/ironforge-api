package br.com.henrique.estudos.ironforge_clientes.repository;

import br.com.henrique.estudos.ironforge_clientes.model.Endereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/dados_enderecos_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EnderecoRepositoryTest {

    @Autowired
    private EnderecoRepository repository;

    @Test
    @DisplayName("findByClienteId - Deve retornar o Endereco correspondente quando o Cliente ID existir")
    void deveRetornarEnderecoQuandoClienteIdExistir() {
        UUID clienteIdExistente = UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d");

        Optional<Endereco> enderecoOptional = repository.findByClienteId(clienteIdExistente);

        assertTrue(enderecoOptional.isPresent());
        assertEquals(clienteIdExistente, enderecoOptional.get().getCliente().getId());
    }

    @Test
    @DisplayName("findByClienteId - Deve retornar Optional vazio quando o Cliente ID não possuir endereço")
    void deveRetornarOptionalVazioQuandoClienteIdNaoExistir() {
        UUID clienteIdInexistente = UUID.fromString("00000000-0000-0000-0000-000000000000");

        Optional<Endereco> enderecoOptional = repository.findByClienteId(clienteIdInexistente);

        assertTrue(enderecoOptional.isEmpty());
    }
}