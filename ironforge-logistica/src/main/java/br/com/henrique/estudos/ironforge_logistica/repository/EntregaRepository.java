package br.com.henrique.estudos.ironforge_logistica.repository;

import br.com.henrique.estudos.ironforge_logistica.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EntregaRepository extends JpaRepository<Entrega, UUID> {
    Optional<Entrega> findByIdAndChaveLogistica(UUID id, String chaveLogistica);
    Optional<Entrega> findByPedidoId(UUID pedidoId);
}
