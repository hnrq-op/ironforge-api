package br.com.henrique.estudos.ironforge_produtos.repository;

import br.com.henrique.estudos.ironforge_produtos.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    Optional<Produto> findBySku(String sku);

    boolean existsBySku(String sku);

    Page<Produto> findByAtivoTrue(Pageable pageable);

    Page<Produto> findByCategoriaAndAtivoTrue(String categoria, Pageable pageable);
}