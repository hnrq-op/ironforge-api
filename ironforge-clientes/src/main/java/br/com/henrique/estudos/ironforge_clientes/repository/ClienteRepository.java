package br.com.henrique.estudos.ironforge_clientes.repository;

import br.com.henrique.estudos.ironforge_clientes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    Optional<Cliente> findByKeycloakId(String keycloakId);

    Optional<Cliente> findByCpf(String cpf);
    Optional<Cliente> findByEmail(String email);

    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByKeycloakId(String keycloakId);
}
