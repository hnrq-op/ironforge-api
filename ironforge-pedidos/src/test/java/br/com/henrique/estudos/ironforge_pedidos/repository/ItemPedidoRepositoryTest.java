package br.com.henrique.estudos.ironforge_pedidos.repository;

import br.com.henrique.estudos.ironforge_pedidos.model.ItemPedido;
import br.com.henrique.estudos.ironforge_pedidos.model.Pedido;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/dados_pedidos_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ItemPedidoRepositoryTest {

    @Autowired
    private ItemPedidoRepository repository;

    @Test
    @DisplayName("findByPedido - Deve retornar a lista de itens correspondentes quando o pedido possuir múltiplos itens")
    void deveRetornarListaDeItensQuandoPedidoPossuirMultiplosItens() {
        Pedido pedido = new Pedido();
        pedido.setId(UUID.fromString("b2222222-2222-2222-2222-222222222222"));

        List<ItemPedido> resultado = repository.findByPedido(pedido);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("findByPedido - Deve retornar a lista com apenas um item quando o pedido possuir somente um item cadastrado")
    void deveRetornarListaComUmItemQuandoPedidoPossuirApenasUmItem() {
        Pedido pedido = new Pedido();
        pedido.setId(UUID.fromString("b1111111-1111-1111-1111-111111111111"));

        List<ItemPedido> resultado = repository.findByPedido(pedido);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(UUID.fromString("c1111111-1111-1111-1111-111111111111"), resultado.get(0).getId());
    }

    @Test
    @DisplayName("findByPedido - Deve retornar lista vazia quando o pedido informado não possuir itens cadastrados")
    void deveRetornarListaVaziaQuandoPedidoNaoPossuirItens() {
        Pedido pedido = new Pedido();
        pedido.setId(UUID.fromString("b9999999-9999-9999-9999-999999999999"));

        List<ItemPedido> resultado = repository.findByPedido(pedido);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}