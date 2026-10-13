package br.com.henrique.estudos.ironforge_pedidos.controller.dto.pedido;

import br.com.henrique.estudos.ironforge_pedidos.controller.dto.itemPedido.ItemPedidoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PedidoRequestDTO(
        @NotNull(message = "O ID do cliente é obrigatório.")
        UUID clienteId,

        String observacoes,

        @NotEmpty(message = "O pedido deve conter pelo menos um item.")
        List<@Valid ItemPedidoRequestDTO> itens
) {}