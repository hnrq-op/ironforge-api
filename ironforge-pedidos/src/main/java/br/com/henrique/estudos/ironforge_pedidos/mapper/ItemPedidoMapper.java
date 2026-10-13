package br.com.henrique.estudos.ironforge_pedidos.mapper;

import br.com.henrique.estudos.ironforge_pedidos.controller.dto.itemPedido.ItemPedidoRequestDTO;
import br.com.henrique.estudos.ironforge_pedidos.controller.dto.itemPedido.ItemPedidoResponseDTO;
import br.com.henrique.estudos.ironforge_pedidos.model.ItemPedido;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    ItemPedido toEntity(ItemPedidoRequestDTO request);

    ItemPedidoResponseDTO toResponse(ItemPedido entity);

    @AfterMapping
    default void calcularSubtotal(@MappingTarget ItemPedido item) {
        if (item.getPrecoUnitario() != null && item.getQuantidade() != null) {
            item.setSubtotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
        }
    }
}