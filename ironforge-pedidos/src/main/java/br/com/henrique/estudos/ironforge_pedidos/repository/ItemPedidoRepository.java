package br.com.henrique.estudos.ironforge_pedidos.repository;

import br.com.henrique.estudos.ironforge_pedidos.model.ItemPedido;
import br.com.henrique.estudos.ironforge_pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, UUID> {
    List<ItemPedido> findByPedido(Pedido pedido);
}
