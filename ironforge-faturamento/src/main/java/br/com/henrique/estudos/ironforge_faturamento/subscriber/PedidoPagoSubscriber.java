package br.com.henrique.estudos.ironforge_faturamento.subscriber;

import br.com.henrique.estudos.ironforge_faturamento.service.GeradorNotaFiscalService;
import br.com.henrique.estudos.ironforge_faturamento.subscriber.representation.PedidoPagoEventRepresentationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PedidoPagoSubscriber {

    private final GeradorNotaFiscalService geradorNotaFiscalService;

    @KafkaListener(
            topics = "${ironforge.config.kafka.topics.pedido-pago}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumir(PedidoPagoEventRepresentationDTO evento) {
        log.info("Evento pedido-pago recebido para o pedido ID: {}", evento.pedidoId());

        geradorNotaFiscalService.gerar(evento);
    }
}