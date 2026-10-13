package br.com.henrique.estudos.ironforge_pedidos.mapper.event;

import br.com.henrique.estudos.ironforge_pedidos.client.representation.cliente.ClienteRepresentationDTO;
import br.com.henrique.estudos.ironforge_pedidos.model.Pedido;
import br.com.henrique.estudos.ironforge_pedidos.publisher.representation.PedidoPagoEventRepresentationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ItemPedidoEventMapper.class, ClienteEventMapper.class})
public interface PedidoPagoEventMapper {

    @Mapping(source = "pedido.id", target = "pedidoId")
    @Mapping(source = "pedido.status", target = "status")
    @Mapping(source = "pedido.valorTotal", target = "valorTotal")
    @Mapping(source = "pedido.chavePagamento", target = "chavePagamento")
    @Mapping(source = "pedido.itens", target = "itens")
    @Mapping(source = "cliente", target = "cliente")
    @Mapping(target = "dataPagamento", expression = "java(java.time.LocalDateTime.now())")
    PedidoPagoEventRepresentationDTO toEvent(Pedido pedido, ClienteRepresentationDTO cliente);
}