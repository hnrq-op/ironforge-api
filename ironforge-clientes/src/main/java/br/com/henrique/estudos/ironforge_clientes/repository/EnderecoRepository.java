package br.com.henrique.estudos.ironforge_clientes.repository;

import br.com.henrique.estudos.ironforge_clientes.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {
    Optional<Endereco> findByClienteId(UUID clienteId);
}
