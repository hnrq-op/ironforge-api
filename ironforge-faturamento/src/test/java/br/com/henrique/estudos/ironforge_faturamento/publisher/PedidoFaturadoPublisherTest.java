package br.com.henrique.estudos.ironforge_faturamento.publisher;

import br.com.henrique.estudos.ironforge_faturamento.publisher.representation.PedidoFaturadoEventRepresentationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PedidoFaturadoPublisherTest {

    @Mock
    private KafkaTemplate<String, PedidoFaturadoEventRepresentationDTO> kafkaTemplate;

    @InjectMocks
    private PedidoFaturadoPublisher pedidoFaturadoPublisher;

    private final String TOPICO = "topico-pedido-faturado-test";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pedidoFaturadoPublisher, "topicoPedidoFaturado", TOPICO);
    }

    @Test
    @DisplayName("Deve enviar o evento de pedido faturado para o Kafka com a chave e tópico corretos")
    void deveEnviarEventoDePedidoFaturadoParaKafkaComSucesso() {
        UUID pedidoId = UUID.randomUUID();
        UUID notaFiscalId = UUID.randomUUID();

        PedidoFaturadoEventRepresentationDTO evento = new PedidoFaturadoEventRepresentationDTO(
                pedidoId,
                notaFiscalId,
                "000000001",
                "35261012345678000195550010000000011000000001",
                "http://localhost:9000/ironforge-faturas/notafiscal_pedido_123.pdf",
                OffsetDateTime.now()
        );

        pedidoFaturadoPublisher.publicar(evento);

        verify(kafkaTemplate).send(eq(TOPICO), eq(pedidoId.toString()), eq(evento));
    }
}