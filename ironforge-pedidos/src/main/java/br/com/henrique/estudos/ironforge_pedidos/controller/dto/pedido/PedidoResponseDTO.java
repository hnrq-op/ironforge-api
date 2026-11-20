package br.com.henrique.estudos.ironforge_pedidos.controller.dto.pedido;

import br.com.henrique.estudos.ironforge_pedidos.client.representation.cliente.ClienteRepresentationDTO;
import br.com.henrique.estudos.ironforge_pedidos.controller.dto.itemPedido.ItemPedidoResponseDTO;
import br.com.henrique.estudos.ironforge_pedidos.model.enums.StatusPedido;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Dados detalhados de um pedido")
public record PedidoResponseDTO(

        @Schema(description = "Identificador único do pedido", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
        UUID id,

        @Schema(description = "Status atual do ciclo de vida do pedido", example = "REALIZADO")
        StatusPedido status,

        @Schema(description = "Valor monetário total do pedido em reais", example = "299.80")
        BigDecimal valorTotal,

        @Schema(description = "Data e hora de criação do pedido", example = "2026-11-20T12:30:00-03:00")
        OffsetDateTime dataCriacao,

        @Schema(description = "Identificador único do cliente", example = "7d444840-9dc0-11d1-b245-5ffdce74fad2")
        UUID clienteId,

        @Schema(description = "Dados completos do cliente (preenchido na consulta de detalhes)")
        ClienteRepresentationDTO dadosCliente,

        @Schema(description = "Chave ou identificador da transação gerada no serviço bancário", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        String chavePagamento,

        @Schema(description = "Observações registradas no pedido", example = "Entregar em horário comercial na portaria.")
        String observacoes,

        @Schema(description = "Código de rastreamento da transportadora após envio", example = "BR123456789IF")
        String codigoRastreio,

        @Schema(description = "URL do PDF da Nota Fiscal após faturamento", example = "https://storage.ironforge.com.br/nf/nota-1024.pdf")
        String urlNf,

        @Schema(description = "Lista de itens vinculados ao pedido")
        List<ItemPedidoResponseDTO> itens

) {}