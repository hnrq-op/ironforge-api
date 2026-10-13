package br.com.henrique.estudos.ironforge_faturamento.publisher;

import br.com.henrique.estudos.ironforge_faturamento.publisher.representation.PedidoFaturadoEventRepresentationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoFaturadoPublisher {

    private final KafkaTemplate<String, PedidoFaturadoEventRepresentationDTO> kafkaTemplate;

    @Value("${ironforge.config.kafka.topics.pedido-faturado}")
    private String topicoPedidoFaturado;

    public void publicar(PedidoFaturadoEventRepresentationDTO evento) {
        log.info("Publicando evento de pedido faturado para o pedido ID: {}", evento.pedidoId());
        kafkaTemplate.send(topicoPedidoFaturado, evento.pedidoId().toString(), evento);
    }
}