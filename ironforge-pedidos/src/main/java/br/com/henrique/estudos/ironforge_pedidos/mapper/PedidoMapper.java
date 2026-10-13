package br.com.henrique.estudos.ironforge_pedidos.mapper;

import br.com.henrique.estudos.ironforge_pedidos.controller.dto.pedido.PedidoRequestDTO;
import br.com.henrique.estudos.ironforge_pedidos.controller.dto.pedido.PedidoResponseDTO;
import br.com.henrique.estudos.ironforge_pedidos.model.Pedido;
import br.com.henrique.estudos.ironforge_pedidos.model.enums.StatusPedido;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", uses = {ItemPedidoMapper.class})
public interface PedidoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "valorTotal", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "chavePagamento", ignore = true)
    @Mapping(target = "codigoRastreio", ignore = true)
    @Mapping(target = "urlNf", ignore = true)
    Pedido toEntity(PedidoRequestDTO request);

    PedidoResponseDTO toResponse(Pedido entity);

    @AfterMapping
    default void afterMapping(@MappingTarget Pedido pedido) {
        if (pedido.getStatus() == null) {
            pedido.setStatus(StatusPedido.REALIZADO);
        }

        BigDecimal total = calcularTotal(pedido);
        pedido.setValorTotal(total);

        if (pedido.getItens() != null) {
            pedido.getItens().forEach(item -> item.setPedido(pedido));
        }
    }

    private static BigDecimal calcularTotal(Pedido pedido) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return pedido.getItens().stream()
                .map(item -> item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    void updateEntityFromDto(PedidoRequestDTO dto, @MappingTarget Pedido pedido);
}