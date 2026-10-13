package br.com.henrique.estudos.ironforge_logistica.subscriber;

import br.com.henrique.estudos.ironforge_logistica.service.EntregaService;
import br.com.henrique.estudos.ironforge_logistica.subscriber.representation.PedidoFaturadoEventRepresentationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoFaturadoSubscriber {

    private final EntregaService service;

    @KafkaListener(
            topics = "${ironforge.config.kafka.topics.pedido-faturado}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumir(PedidoFaturadoEventRepresentationDTO evento){
        log.info("Evento pedido-faturado recebido para o pedido ID: {}", evento.pedidoId());
        service.criarEntrega(evento);
    }
}
