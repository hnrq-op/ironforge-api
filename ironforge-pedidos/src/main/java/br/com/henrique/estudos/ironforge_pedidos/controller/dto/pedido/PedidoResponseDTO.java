package br.com.henrique.estudos.ironforge_pedidos.controller.dto.pedido;

import br.com.henrique.estudos.ironforge_pedidos.client.representation.cliente.ClienteRepresentationDTO;
import br.com.henrique.estudos.ironforge_pedidos.controller.dto.itemPedido.ItemPedidoResponseDTO;
import br.com.henrique.estudos.ironforge_pedidos.model.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PedidoResponseDTO(
        UUID id,
        StatusPedido status,
        BigDecimal valorTotal,
        OffsetDateTime dataCriacao,
        UUID clienteId,
        ClienteRepresentationDTO dadosCliente,
        String chavePagamento,
        String observacoes,
        String codigoRastreio,
        String urlNf,
        List<ItemPedidoResponseDTO> itens
) {}