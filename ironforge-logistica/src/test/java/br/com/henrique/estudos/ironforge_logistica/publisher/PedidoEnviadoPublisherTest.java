package br.com.henrique.estudos.ironforge_logistica.publisher;

import br.com.henrique.estudos.ironforge_logistica.model.enums.StatusEntrega;
import br.com.henrique.estudos.ironforge_logistica.publisher.representation.PedidoEnviadoEventRepresentationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PedidoEnviadoPublisherTest {

    @Mock
    private KafkaTemplate<String, PedidoEnviadoEventRepresentationDTO> kafkaTemplate;

    @InjectMocks
    private PedidoEnviadoPublisher publisher;

    private static final String TOPICO = "ironforge.pedidos.pedido-enviado";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(publisher, "topicoPedidoEnviado", TOPICO);
    }

    @Test
    @DisplayName("publicar - Deve invocar send no KafkaTemplate passando o tópico, o pedidoId como key e o evento")
    void devePublicarEventoNoKafkaComSucesso() {
        UUID pedidoId = UUID.randomUUID();
        PedidoEnviadoEventRepresentationDTO evento = new PedidoEnviadoEventRepresentationDTO(
                pedidoId,
                "BR123456789BR",
                StatusEntrega.ENVIADO,
                "Jadlog",
                OffsetDateTime.now(),
                LocalDate.now().plusDays(5),
                OffsetDateTime.now()
        );

        publisher.publicar(evento);

        verify(kafkaTemplate).send(TOPICO, pedidoId.toString(), evento);
    }
}