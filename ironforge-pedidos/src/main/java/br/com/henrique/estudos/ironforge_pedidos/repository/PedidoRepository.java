package br.com.henrique.estudos.ironforge_pedidos.repository;

import br.com.henrique.estudos.ironforge_pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    Optional<Pedido> findByIdAndChavePagamento(UUID id, String chavePagamento);
}
