package br.com.henrique.estudos.ironforge_logistica.subscriber;

import br.com.henrique.estudos.ironforge_logistica.service.EntregaService;
import br.com.henrique.estudos.ironforge_logistica.subscriber.representation.PedidoFaturadoEventRepresentationDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PedidoFaturadoSubscriberTest {

    @Mock
    private EntregaService service;

    @InjectMocks
    private PedidoFaturadoSubscriber subscriber;

    @Test
    @DisplayName("consumir - Deve repassar o evento de pedido faturado para o service criar a entrega")
    void deveRepassarEventoParaService() {
        UUID pedidoId = UUID.randomUUID();
        PedidoFaturadoEventRepresentationDTO evento = new PedidoFaturadoEventRepresentationDTO(
                pedidoId,
                UUID.randomUUID(),
                "12345",
                "chave-nfe-123",
                "http://pdf.url",
                OffsetDateTime.now()
        );

        subscriber.consumir(evento);

        verify(service).criarEntrega(evento);
    }
}