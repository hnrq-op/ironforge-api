package br.com.henrique.estudos.ironforge_pedidos.publisher;

import br.com.henrique.estudos.ironforge_pedidos.publisher.representation.PedidoPagoEventRepresentationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PagamentoPublisherTest {

    @Mock
    private KafkaTemplate<String, PedidoPagoEventRepresentationDTO> kafkaTemplate;

    @InjectMocks
    private PagamentoPublisher pagamentoPublisher;

    private final String TOPICO = "topico-pedido-pago-test";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pagamentoPublisher, "topicoPedidoPago", TOPICO);
    }

    @Test
    @DisplayName("publicar - Deve enviar o evento de pedido pago para o Kafka com a chave e tópico corretos")
    void publicar_DeveEnviarEventoParaKafkaComSucesso() {
        UUID pedidoId = UUID.randomUUID();
        PedidoPagoEventRepresentationDTO evento = new PedidoPagoEventRepresentationDTO(
                pedidoId,
                "PAGO",
                BigDecimal.valueOf(250.00),
                "chave-pagamento-123",
                LocalDateTime.now(),
                null,
                Collections.emptyList()
        );

        pagamentoPublisher.publicar(evento);

        verify(kafkaTemplate).send(eq(TOPICO), eq(pedidoId.toString()), eq(evento));
    }
}