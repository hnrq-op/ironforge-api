package br.com.henrique.estudos.ironforge_pedidos.subscriber;

import br.com.henrique.estudos.ironforge_pedidos.model.enums.StatusPedido;
import br.com.henrique.estudos.ironforge_pedidos.service.AtualizacaoStatusPedidoService;
import br.com.henrique.estudos.ironforge_pedidos.subscriber.representation.PedidoEnviadoEventRepresentation;
import br.com.henrique.estudos.ironforge_pedidos.subscriber.representation.PedidoFaturadoEventRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AtualizacaoStatusPedidoSubscriber {

    private final AtualizacaoStatusPedidoService service;

    @KafkaListener(
            topics = "${ironforge.config.kafka.topics.pedido-faturado}",
            containerFactory = "faturadoContainerFactory"
    )
    public void receberPedidoFaturado(PedidoFaturadoEventRepresentation evento) {
        log.info("Recebendo confirmação de faturamento para o pedido: {}", evento.pedidoId());
        try {
            service.atualizarStatus(
                    evento.pedidoId(),
                    StatusPedido.FATURADO,
                    evento.pdfUrl(),
                    null
            );
            log.info("Pedido {} atualizado para FATURADO com sucesso!", evento.pedidoId());
        } catch (Exception e) {
            log.error("Erro ao atualizar faturamento do pedido {}: {}", evento.pedidoId(), e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = "${ironforge.config.kafka.topics.pedido-enviado}",
            containerFactory = "enviadoContainerFactory"
    )
    public void receberPedidoEnviado(PedidoEnviadoEventRepresentation evento) {
        log.info("Recebendo confirmação de envio para o pedido: {}", evento.pedidoId());
        try {
            service.atualizarStatus(
                    evento.pedidoId(),
                    StatusPedido.ENVIADO,
                    null,
                    evento.codigoRastreio()
            );
            log.info("Pedido {} atualizado para ENVIADO com código de rastreio: {}", evento.pedidoId(), evento.codigoRastreio());
        } catch (Exception e) {
            log.error("Erro ao atualizar envio do pedido {}: {}", evento.pedidoId(), e.getMessage(), e);
        }
    }
}