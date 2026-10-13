package br.com.henrique.estudos.ironforge_faturamento.subscriber;

import br.com.henrique.estudos.ironforge_faturamento.service.GeradorNotaFiscalService;
import br.com.henrique.estudos.ironforge_faturamento.subscriber.representation.PedidoPagoEventRepresentationDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PedidoPagoSubscriberTest {

    @Mock
    private GeradorNotaFiscalService geradorNotaFiscalService;

    @InjectMocks
    private PedidoPagoSubscriber pedidoPagoSubscriber;

    @Test
    @DisplayName("Deve delegar o processamento do evento de pedido pago para o GeradorNotaFiscalService")
    void deveDelegarProcessamentoParaGeradorNotaFiscalComSucesso() {
        UUID pedidoId = UUID.randomUUID();
        PedidoPagoEventRepresentationDTO evento = new PedidoPagoEventRepresentationDTO(
                pedidoId,
                "PAGO",
                BigDecimal.valueOf(199.90),
                "PAY-98765",
                LocalDateTime.now(),
                null,
                Collections.emptyList()
        );

        pedidoPagoSubscriber.consumir(evento);

        verify(geradorNotaFiscalService).gerar(evento);
    }

    @Test
    @DisplayName("Deve propagar exceção quando o GeradorNotaFiscalService falhar no faturamento")
    void devePropagarExcecaoQuandoGeradorNotaFiscalFalhar() {
        UUID pedidoId = UUID.randomUUID();
        PedidoPagoEventRepresentationDTO evento = new PedidoPagoEventRepresentationDTO(
                pedidoId,
                "PAGO",
                BigDecimal.valueOf(199.90),
                "PAY-98765",
                LocalDateTime.now(),
                null,
                Collections.emptyList()
        );

        doThrow(new RuntimeException("Erro ao gerar PDF da nota fiscal"))
                .when(geradorNotaFiscalService).gerar(evento);

        assertThatThrownBy(() -> pedidoPagoSubscriber.consumir(evento))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Erro ao gerar PDF da nota fiscal");

        verify(geradorNotaFiscalService).gerar(evento);
    }
}