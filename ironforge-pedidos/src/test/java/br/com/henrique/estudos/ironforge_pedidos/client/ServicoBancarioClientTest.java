package br.com.henrique.estudos.ironforge_pedidos.client;

import br.com.henrique.estudos.ironforge_pedidos.model.Pedido;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class ServicoBancarioClientTest {

    private final ServicoBancarioClient servicoBancarioClient = new ServicoBancarioClient();

    @Test
    @DisplayName("solicitarPagamento - Deve retornar uma chave de transação UUID válida ao solicitar pagamento")
    void solicitarPagamento_DeveRetornarChaveTransacaoValida() {
        Pedido pedido = new Pedido();

        String resultado = servicoBancarioClient.solicitarPagamento(pedido);

        assertThat(resultado)
                .isNotNull()
                .isNotBlank();

        assertThatNoException().isThrownBy(() -> UUID.fromString(resultado));
    }
}