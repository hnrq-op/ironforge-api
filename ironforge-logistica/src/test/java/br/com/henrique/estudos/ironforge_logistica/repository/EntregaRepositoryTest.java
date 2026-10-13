package br.com.henrique.estudos.ironforge_logistica.repository;

import br.com.henrique.estudos.ironforge_logistica.model.Entrega;
import br.com.henrique.estudos.ironforge_logistica.model.enums.StatusEntrega;
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
@Sql(scripts = "/sql/dados_entregas_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EntregaRepositoryTest {

    @Autowired
    private EntregaRepository repository;

    @Test
    @DisplayName("findByIdAndChaveLogistica - Deve retornar entrega quando o ID e a chave logística existirem e coincidirem")
    void deveRetornarEntregaQuandoIdEChaveLogisticaCoincidirem() {
        UUID entregaId = UUID.fromString("e1111111-1111-1111-1111-111111111111");
        String chaveLogistica = "chave-logistica-valida-1";

        Optional<Entrega> resultado = repository.findByIdAndChaveLogistica(entregaId, chaveLogistica);

        assertTrue(resultado.isPresent());
        assertEquals(entregaId, resultado.get().getId());
        assertEquals("chave-logistica-valida-1", resultado.get().getChaveLogistica());
        assertEquals(StatusEntrega.ENVIADO, resultado.get().getStatus());
    }

    @Test
    @DisplayName("findByIdAndChaveLogistica - Deve retornar Optional vazio quando o ID existir mas a chave logística for incorreta")
    void deveRetornarVazioQuandoChaveLogisticaForIncorreta() {
        UUID entregaId = UUID.fromString("e1111111-1111-1111-1111-111111111111");
        String chaveInvalida = "chave-logistica-incorreta";

        Optional<Entrega> resultado = repository.findByIdAndChaveLogistica(entregaId, chaveInvalida);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("findByIdAndChaveLogistica - Deve retornar Optional vazio quando a entrega não for encontrada pelo ID")
    void deveRetornarVazioQuandoEntregaNaoExistirPorId() {
        UUID idInexistente = UUID.fromString("e9999999-9999-9999-9999-999999999999");
        String chave = "chave-logistica-valida-1";

        Optional<Entrega> resultado = repository.findByIdAndChaveLogistica(idInexistente, chave);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("findByPedidoId - Deve retornar entrega correspondente quando existir um registro associado ao ID do pedido")
    void deveRetornarEntregaQuandoPedidoIdExistir() {
        UUID pedidoId = UUID.fromString("a1111111-1111-1111-1111-111111111111");

        Optional<Entrega> resultado = repository.findByPedidoId(pedidoId);

        assertTrue(resultado.isPresent());
        assertEquals(pedidoId, resultado.get().getPedidoId());
        assertEquals(UUID.fromString("e1111111-1111-1111-1111-111111111111"), resultado.get().getId());
    }

    @Test
    @DisplayName("findByPedidoId - Deve retornar Optional vazio quando não houver entrega para o ID do pedido informado")
    void deveRetornarVazioQuandoPedidoIdNaoExistir() {
        UUID pedidoIdInexistente = UUID.fromString("a9999999-9999-9999-9999-999999999999");

        Optional<Entrega> resultado = repository.findByPedidoId(pedidoIdInexistente);

        assertTrue(resultado.isEmpty());
    }
}