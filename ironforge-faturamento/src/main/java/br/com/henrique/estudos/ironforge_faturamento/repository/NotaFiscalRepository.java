package br.com.henrique.estudos.ironforge_faturamento.repository;

import br.com.henrique.estudos.ironforge_faturamento.model.NotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, UUID> {
    boolean existsByPedidoId(UUID pedidoId);
}