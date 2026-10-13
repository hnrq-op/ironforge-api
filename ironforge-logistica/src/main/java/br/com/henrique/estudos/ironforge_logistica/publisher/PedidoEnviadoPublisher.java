package br.com.henrique.estudos.ironforge_logistica.publisher;

import br.com.henrique.estudos.ironforge_logistica.publisher.representation.PedidoEnviadoEventRepresentationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoEnviadoPublisher {

    private final KafkaTemplate<String, PedidoEnviadoEventRepresentationDTO> kafkaTemplate;

    @Value("${ironforge.config.kafka.topics.pedido-enviado}")
    private String topicoPedidoEnviado;

    public void publicar(PedidoEnviadoEventRepresentationDTO evento){
        log.info("Publicando evento de pedido enviado para o pedido ID: {}", evento.pedidoId());
        kafkaTemplate.send(topicoPedidoEnviado, evento.pedidoId().toString(), evento);
    }
}
