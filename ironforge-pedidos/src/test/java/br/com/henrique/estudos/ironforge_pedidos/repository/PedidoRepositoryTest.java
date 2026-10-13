package br.com.henrique.estudos.ironforge_pedidos.repository;

import br.com.henrique.estudos.ironforge_pedidos.model.Pedido;
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
@Sql(scripts = "/sql/dados_pedidos_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository repository;

    @Test
    @DisplayName("findByIdAndChavePagamento - Deve retornar o Pedido correspondente quando o ID e a chave de pagamento existirem")
    void deveRetornarPedidoQuandoIdEChavePagamentoExistirem() {
        UUID idExistente = UUID.fromString("b2222222-2222-2222-2222-222222222222");
        String chaveExistente = "PIX-KEY-123456789";

        Optional<Pedido> pedidoOptional = repository.findByIdAndChavePagamento(idExistente, chaveExistente);

        assertTrue(pedidoOptional.isPresent());
        assertEquals(idExistente, pedidoOptional.get().getId());
        assertEquals(chaveExistente, pedidoOptional.get().getChavePagamento());
    }

    @Test
    @DisplayName("findByIdAndChavePagamento - Deve retornar Optional vazio quando o ID existir mas a chave de pagamento for incorreta")
    void deveRetornarOptionalVazioQuandoChavePagamentoForIncorreta() {
        UUID idExistente = UUID.fromString("b2222222-2222-2222-2222-222222222222");
        String chaveIncorreta = "CHAVE-INVALIDA-999";

        Optional<Pedido> pedidoOptional = repository.findByIdAndChavePagamento(idExistente, chaveIncorreta);

        assertTrue(pedidoOptional.isEmpty());
    }

    @Test
    @DisplayName("findByIdAndChavePagamento - Deve retornar Optional vazio quando o ID não existir")
    void deveRetornarOptionalVazioQuandoIdNaoExistir() {
        UUID idInexistente = UUID.fromString("99999999-9999-9999-9999-999999999999");
        String chaveExistente = "PIX-KEY-123456789";

        Optional<Pedido> pedidoOptional = repository.findByIdAndChavePagamento(idInexistente, chaveExistente);

        assertTrue(pedidoOptional.isEmpty());
    }

    @Test
    @DisplayName("findByIdAndChavePagamento - Deve retornar Optional vazio quando o pedido possuir chave de pagamento nula no banco")
    void deveRetornarOptionalVazioQuandoChavePagamentoForNulaNoBanco() {
        UUID idPedidoSemChave = UUID.fromString("b1111111-1111-1111-1111-111111111111");
        String chaveExistente = "PIX-KEY-123456789";

        Optional<Pedido> pedidoOptional = repository.findByIdAndChavePagamento(idPedidoSemChave, chaveExistente);

        assertTrue(pedidoOptional.isEmpty());
    }
}