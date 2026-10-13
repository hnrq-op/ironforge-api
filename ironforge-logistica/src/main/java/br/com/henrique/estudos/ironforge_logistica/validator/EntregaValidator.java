package br.com.henrique.estudos.ironforge_logistica.validator;

import br.com.henrique.estudos.ironforge_logistica.controller.dto.RecebimentoCallbackLogisticaDTO;
import br.com.henrique.estudos.ironforge_logistica.exceptions.ValidationException;
import br.com.henrique.estudos.ironforge_logistica.model.Entrega;
import br.com.henrique.estudos.ironforge_logistica.repository.EntregaRepository;
import br.com.henrique.estudos.ironforge_logistica.subscriber.representation.PedidoFaturadoEventRepresentationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EntregaValidator {

    private final EntregaRepository repository;

    public void validar(PedidoFaturadoEventRepresentationDTO evento) {
        validarEvento(evento);
        validarDuplicidade(evento.pedidoId());
    }

    public Entrega validar(RecebimentoCallbackLogisticaDTO dto) {
        Entrega entrega = buscarEntregaExistente(dto.pedidoId());
        validarChaveLogistica(entrega, dto.chaveLogistica());
        return entrega;
    }

    private void validarEvento(PedidoFaturadoEventRepresentationDTO evento) {
        if (evento == null || evento.pedidoId() == null) {
            throw new ValidationException("pedidoId", "O ID do pedido é obrigatório no evento.");
        }
    }

    private void validarDuplicidade(UUID pedidoId) {
        if (repository.findByPedidoId(pedidoId).isPresent()) {
            log.warn("Tentativa de criar entrega já existente para o pedido ID: {}", pedidoId);
            throw new ValidationException("pedidoId", "Já existe uma entrega registrada para este pedido.");
        }
    }

    private Entrega buscarEntregaExistente(UUID pedidoId) {
        return repository.findByPedidoId(pedidoId)
                .orElseThrow(() -> {
                    log.warn("Entrega não encontrada para o pedido ID: {}", pedidoId);
                    return new ValidationException("pedidoId", "Entrega não encontrada para o pedido informado.");
                });
    }

    private void validarChaveLogistica(Entrega entrega, String chaveLogistica) {
        if (entrega.getChaveLogistica() == null || !entrega.getChaveLogistica().equals(chaveLogistica)) {
            log.error("Acesso negado: Chave de logística incorreta para o pedido ID: {}", entrega.getPedidoId());
            throw new ValidationException("chaveLogistica", "Chave de logística inválida para este pedido.");
        }
    }
}