package br.com.henrique.estudos.ironforge_logistica.service;

import br.com.henrique.estudos.ironforge_logistica.client.ServicoLogisticaClient;
import br.com.henrique.estudos.ironforge_logistica.controller.dto.RecebimentoCallbackLogisticaDTO;
import br.com.henrique.estudos.ironforge_logistica.mapper.EntregaMapper;
import br.com.henrique.estudos.ironforge_logistica.model.Entrega;
import br.com.henrique.estudos.ironforge_logistica.model.enums.StatusEntrega;
import br.com.henrique.estudos.ironforge_logistica.publisher.PedidoEnviadoPublisher;
import br.com.henrique.estudos.ironforge_logistica.publisher.representation.PedidoEnviadoEventRepresentationDTO;
import br.com.henrique.estudos.ironforge_logistica.repository.EntregaRepository;
import br.com.henrique.estudos.ironforge_logistica.subscriber.representation.PedidoFaturadoEventRepresentationDTO;
import br.com.henrique.estudos.ironforge_logistica.validator.EntregaValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntregaService {

    private final EntregaRepository repository;
    private final EntregaValidator validator;
    private final EntregaMapper mapper;
    private final ServicoLogisticaClient servicoLogisticaClient;
    private final PedidoEnviadoPublisher pedidoEnviadoPublisher;

    @Transactional
    public void criarEntrega(PedidoFaturadoEventRepresentationDTO evento) {
        log.info("Iniciando criação de entrega para o pedido ID: {}", evento.pedidoId());

        validator.validar(evento);

        Entrega entrega = new Entrega();
        entrega.setPedidoId(evento.pedidoId());
        entrega.setStatus(StatusEntrega.AGUARDANDO_TRANSPORTADORA);

        String chaveLogistica = servicoLogisticaClient.solicitarEntrega(entrega);
        entrega.setChaveLogistica(chaveLogistica);

        repository.save(entrega);
        log.info("Entrega criada com sucesso para o pedido ID: {}. Chave logística gerada: {}", evento.pedidoId(), chaveLogistica);
    }

    @Transactional
    public void processarCallbackTransportadora(RecebimentoCallbackLogisticaDTO dto) {
        log.info("Processando callback do webhook para a entrega ID: {}", dto.id());

        Entrega entrega = repository.findByIdAndChaveLogistica(dto.id(), dto.chaveLogistica())
                .orElseThrow(() -> {
                    log.error("Entrega não encontrada para a entrega ID {} e chave logística {}", dto.id(), dto.chaveLogistica());
                    return new EntityNotFoundException(
                            String.format("Entrega não encontrada para a entrega ID %s com a chave logística fornecida.", dto.id())
                    );
                });

        boolean eraStatusDiferenteDeEnviado = entrega.getStatus() != StatusEntrega.ENVIADO;
        boolean virouEnviado = dto.status() == StatusEntrega.ENVIADO;

        entrega.setStatus(dto.status());
        entrega.setCodigoRastreio(dto.codigoRastreio());

        if (dto.transportadora() != null) {
            entrega.setTransportadora(dto.transportadora());
        }

        if (dto.dataPrevisao() != null) {
            entrega.setDataPrevisao(dto.dataPrevisao());
        }

        if (virouEnviado && entrega.getDataEnvio() == null) {
            entrega.setDataEnvio(OffsetDateTime.now());
        }

        repository.saveAndFlush(entrega);
        log.info("Status da entrega do pedido ID {} atualizado para: {}", entrega.getPedidoId(), entrega.getStatus());

        if (eraStatusDiferenteDeEnviado && virouEnviado) {
            notificarPedidoEnviado(entrega);
        }
    }

    private void notificarPedidoEnviado(Entrega entrega) {
        PedidoEnviadoEventRepresentationDTO evento = mapper.toEventDto(entrega);
        pedidoEnviadoPublisher.publicar(evento);
        log.info("Evento de pedido enviado publicado no Kafka para o pedido ID: {}", entrega.getPedidoId());
    }

    @Transactional(readOnly = true)
    public Entrega buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entrega não encontrada com o ID: " + id));
    }
}