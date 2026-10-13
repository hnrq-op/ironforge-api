package br.com.henrique.estudos.ironforge_clientes.repository;

import br.com.henrique.estudos.ironforge_clientes.model.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/dados_clientes_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository repository;

    @Test
    @DisplayName("findByKeycloakId - Deve retornar o Cliente correspondente quando o Keycloak ID existir")
    void deveRetornarClienteQuandoKeycloakIdExistir() {
        String keycloakIdExistente = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d";

        Optional<Cliente> clienteOptional = repository.findByKeycloakId(keycloakIdExistente);

        assertTrue(clienteOptional.isPresent());
        assertEquals(keycloakIdExistente, clienteOptional.get().getKeycloakId());
    }

    @Test
    @DisplayName("findByKeycloakId - Deve retornar Optional vazio quando o Keycloak ID não existir")
    void deveRetornarOptionalVazioQuandoKeycloakIdNaoExistir() {
        String keycloakIdInexistente = "00000000-0000-0000-0000-000000000000";

        Optional<Cliente> clienteOptional = repository.findByKeycloakId(keycloakIdInexistente);

        assertTrue(clienteOptional.isEmpty());
    }

    @Test
    @DisplayName("findByCpf - Deve retornar o Cliente correspondente quando o CPF existir")
    void deveRetornarClienteQuandoCpfExistir() {
        String cpfExistente = "12345678900";

        Optional<Cliente> clienteOptional = repository.findByCpf(cpfExistente);

        assertTrue(clienteOptional.isPresent());
        assertEquals(cpfExistente, clienteOptional.get().getCpf());
    }

    @Test
    @DisplayName("findByCpf - Deve retornar Optional vazio quando o CPF não existir")
    void deveRetornarOptionalVazioQuandoCpfNaoExistir() {
        String cpfInexistente = "00000000000";

        Optional<Cliente> clienteOptional = repository.findByCpf(cpfInexistente);

        assertTrue(clienteOptional.isEmpty());
    }

    @Test
    @DisplayName("findByEmail - Deve retornar o Cliente correspondente quando o Email existir")
    void deveRetornarClienteQuandoEmailExistir() {
        String emailExistente = "cliente@teste.com";

        Optional<Cliente> clienteOptional = repository.findByEmail(emailExistente);

        assertTrue(clienteOptional.isPresent());
        assertEquals(emailExistente, clienteOptional.get().getEmail());
    }

    @Test
    @DisplayName("findByEmail - Deve retornar Optional vazio quando o Email não existir")
    void deveRetornarOptionalVazioQuandoEmailNaoExistir() {
        String emailInexistente = "inexistente@teste.com";

        Optional<Cliente> clienteOptional = repository.findByEmail(emailInexistente);

        assertTrue(clienteOptional.isEmpty());
    }

    @Test
    @DisplayName("existsByCpf - Deve retornar true ao verificar existência de CPF cadastrado")
    void deveRetornarTrueQuandoCpfExistir() {
        String cpfExistente = "12345678900";

        boolean existe = repository.existsByCpf(cpfExistente);

        assertTrue(existe);
    }

    @Test
    @DisplayName("existsByCpf - Deve retornar false ao verificar existência de CPF não cadastrado")
    void deveRetornarFalseQuandoCpfNaoExistir() {
        String cpfInexistente = "00000000000";

        boolean existe = repository.existsByCpf(cpfInexistente);

        assertFalse(existe);
    }

    @Test
    @DisplayName("existsByEmail - Deve retornar true ao verificar existência de Email cadastrado")
    void deveRetornarTrueQuandoEmailExistir() {
        String emailExistente = "cliente@teste.com";

        boolean existe = repository.existsByEmail(emailExistente);

        assertTrue(existe);
    }

    @Test
    @DisplayName("existsByEmail - Deve retornar false ao verificar existência de Email não cadastrado")
    void deveRetornarFalseQuandoEmailNaoExistir() {
        String emailInexistente = "inexistente@teste.com";

        boolean existe = repository.existsByEmail(emailInexistente);

        assertFalse(existe);
    }

    @Test
    @DisplayName("existsByKeycloakId - Deve retornar true ao verificar existência de Keycloak ID cadastrado")
    void deveRetornarTrueQuandoKeycloakIdExistir() {
        String keycloakIdExistente = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d";

        boolean existe = repository.existsByKeycloakId(keycloakIdExistente);

        assertTrue(existe);
    }

    @Test
    @DisplayName("existsByKeycloakId - Deve retornar false ao verificar existência de Keycloak ID não cadastrado")
    void deveRetornarFalseQuandoKeycloakIdNaoExistir() {
        String keycloakIdInexistente = "00000000-0000-0000-0000-000000000000";

        boolean existe = repository.existsByKeycloakId(keycloakIdInexistente);

        assertFalse(existe);
    }
}