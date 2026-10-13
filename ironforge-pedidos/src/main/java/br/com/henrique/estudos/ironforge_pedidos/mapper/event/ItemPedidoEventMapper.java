package br.com.henrique.estudos.ironforge_pedidos.mapper.event;

import br.com.henrique.estudos.ironforge_pedidos.model.ItemPedido;
import br.com.henrique.estudos.ironforge_pedidos.publisher.representation.ItemPedidoEventRepresentationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemPedidoEventMapper {

    @Mapping(source = "precoUnitario", target = "precoUnitario")
    ItemPedidoEventRepresentationDTO toDTO(ItemPedido item);
}