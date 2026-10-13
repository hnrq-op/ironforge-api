package br.com.henrique.estudos.ironforge_pedidos.subscriber;

import br.com.henrique.estudos.ironforge_pedidos.model.enums.StatusPedido;
import br.com.henrique.estudos.ironforge_pedidos.service.AtualizacaoStatusPedidoService;
import br.com.henrique.estudos.ironforge_pedidos.subscriber.representation.PedidoFaturadoEventRepresentation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AtualizacaoStatusPedidoSubscriberTest {

    @Mock
    private AtualizacaoStatusPedidoService service;

    @InjectMocks
    private AtualizacaoStatusPedidoSubscriber subscriber;

    @Test
    @DisplayName("receberAtualizacao - Deve delegar a atualização de status para o serviço com sucesso")
    void receberAtualizacao_DeveDelegarAtualizacaoParaService() {
        UUID pedidoId = UUID.randomUUID();
        UUID notaFiscalId = UUID.randomUUID();
        PedidoFaturadoEventRepresentation atualizacao = new PedidoFaturadoEventRepresentation(
                pedidoId,
                notaFiscalId,
                "1",
                "BR123456789BR",
                "http://nota.fiscal/123",
                OffsetDateTime.now()
        );

        subscriber.receberPedidoFaturado(atualizacao);

        verify(service).atualizarStatus(
                pedidoId,
                StatusPedido.FATURADO,
                "http://nota.fiscal/123",
                "BR123456789BR"
        );
    }

    @Test
    @DisplayName("receberAtualizacao - Deve capturar exceção e não lançar erro ao falhar o processamento do serviço")
    void receberAtualizacao_DeveCapturarExcecaoQuandoServiceFalhar() {
        UUID pedidoId = UUID.randomUUID();
        UUID notaFiscalId = UUID.randomUUID();
        PedidoFaturadoEventRepresentation atualizacao = new PedidoFaturadoEventRepresentation(
                pedidoId,
                notaFiscalId,
                "1",
                "BR123456789BR",
                "http://nota.fiscal/123",
                OffsetDateTime.now()
        );

        doThrow(new RuntimeException("Erro ao atualizar banco"))
                .when(service).atualizarStatus(
                        pedidoId,
                        StatusPedido.ENVIADO,
                        null,
                        "BR123456789BR"
                );

        assertThatNoException().isThrownBy(() -> subscriber.receberPedidoFaturado(atualizacao));

        verify(service).atualizarStatus(
                pedidoId,
                StatusPedido.ENVIADO,
                null,
                "BR123456789BR"
        );
    }
}