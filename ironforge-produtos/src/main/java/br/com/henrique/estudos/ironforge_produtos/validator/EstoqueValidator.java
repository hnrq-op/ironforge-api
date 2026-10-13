package br.com.henrique.estudos.ironforge_produtos.validator;

import br.com.henrique.estudos.ironforge_produtos.exceptions.RegistroDuplicadoException;
import br.com.henrique.estudos.ironforge_produtos.model.Estoque;
import br.com.henrique.estudos.ironforge_produtos.repository.EstoqueRepository;
import br.com.henrique.estudos.ironforge_produtos.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstoqueValidator {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;

    public void validar(Estoque estoque) {
        validarProdutoExistente(estoque);
        validarEstoqueUnicoPorProduto(estoque);
        validarQuantidades(estoque);
    }

    private void validarProdutoExistente(Estoque estoque) {
        if (estoque.getProduto() == null || estoque.getProduto().getId() == null) {
            throw new IllegalArgumentException("É necessário informar um Produto válido para o estoque.");
        }

        if (!produtoRepository.existsById(estoque.getProduto().getId())) {
            throw new EntityNotFoundException("Produto não encontrado no banco de dados.");
        }
    }

    private void validarEstoqueUnicoPorProduto(Estoque estoque) {
        estoqueRepository.findByProdutoId(estoque.getProduto().getId())
                .ifPresent(estoqueExistente -> {
                    if (estoque.getId() == null || !estoque.getId().equals(estoqueExistente.getId())) {
                        throw new RegistroDuplicadoException(
                                "O produto de ID " + estoque.getProduto().getId() + " já possui um estoque cadastrado."
                        );
                    }
                });
    }

    private void validarQuantidades(Estoque estoque) {
        if (estoque.getQuantidadeDisponivel() != null && estoque.getQuantidadeDisponivel() < 0) {
            throw new IllegalArgumentException("A quantidade disponível não pode ser negativa.");
        }

        if (estoque.getQuantidadeReservada() != null && estoque.getQuantidadeReservada() < 0) {
            throw new IllegalArgumentException("A quantidade reservada não pode ser negativa.");
        }
    }
}