package br.com.henrique.estudos.ironforge_produtos.repository;

import br.com.henrique.estudos.ironforge_produtos.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, UUID> {

    Optional<Estoque> findByProdutoId(UUID produtoId);
}