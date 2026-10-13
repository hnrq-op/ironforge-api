package br.com.henrique.estudos.ironforge_produtos.validator;

import br.com.henrique.estudos.ironforge_produtos.exceptions.RegistroDuplicadoException;
import br.com.henrique.estudos.ironforge_produtos.model.Produto;
import br.com.henrique.estudos.ironforge_produtos.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProdutoValidator {

    private final ProdutoRepository repository;

    public void validar(Produto produto){
        validarSkuUnico(produto);
    }

    private void validarSkuUnico(Produto produto) {
        repository.findBySku(produto.getSku())
                .ifPresent( produtoExistente -> {
                    if (produto.getId() == null || !produto.getId().equals(produtoExistente.getId())){
                        throw new RegistroDuplicadoException("O SKU " + produto.getSku() + " já está em uso!");
                    }
                });
    }
}
