package br.com.henrique.estudos.ironforge_pedidos.publisher;

import br.com.henrique.estudos.ironforge_pedidos.publisher.representation.PedidoPagoEventRepresentationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PagamentoPublisher {

    private final KafkaTemplate<String, PedidoPagoEventRepresentationDTO> kafkaTemplate;

    @Value("${ironforge.config.kafka.topics.pedido-pago}")
    private String topicoPedidoPago;

    public void publicar(PedidoPagoEventRepresentationDTO evento) {
        log.info("Publicando evento de pedido pago para o pedido ID: {}", evento.pedidoId());
        kafkaTemplate.send(topicoPedidoPago, evento.pedidoId().toString(), evento);
    }
}