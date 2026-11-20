package br.com.henrique.estudos.ironforge_pedidos.controller.dto.pedido;

import br.com.henrique.estudos.ironforge_pedidos.controller.dto.itemPedido.ItemPedidoRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Schema(description = "Dados de entrada para abertura ou atualização de um pedido")
public record PedidoRequestDTO(

        @Schema(description = "Identificador único (UUID) do cliente comprador", example = "7d444840-9dc0-11d1-b245-5ffdce74fad2")
        @NotNull(message = "O ID do cliente é obrigatório.")
        UUID clienteId,

        @Schema(description = "Observações opcionais sobre entrega ou embalagem", example = "Entregar em horário comercial na portaria.")
        String observacoes,

        @Schema(description = "Lista de itens do pedido (deve conter ao menos 1 item)")
        @NotEmpty(message = "O pedido deve conter pelo menos um item.")
        List<@Valid ItemPedidoRequestDTO> itens

) {}