package br.com.henrique.estudos.ironforge_produtos.service;

import br.com.henrique.estudos.ironforge_produtos.controller.dto.produto.ProdutoRequestDTO;
import br.com.henrique.estudos.ironforge_produtos.mapper.ProdutoMapper;
import br.com.henrique.estudos.ironforge_produtos.model.Produto;
import br.com.henrique.estudos.ironforge_produtos.repository.ProdutoRepository;
import br.com.henrique.estudos.ironforge_produtos.validator.ProdutoValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository repository;
    private final ProdutoValidator validator;
    private final ProdutoMapper mapper;

    @Transactional
    public void salvar(Produto produto) {
        validator.validar(produto);
        repository.save(produto);
    }

    @Transactional
    public void atualizar(UUID id, ProdutoRequestDTO dto) {
        if (id == null){
            throw new IllegalArgumentException("Para atualizar é necessário que o produto esteja salvo na base (ID não pode ser nulo)");
        }

        Produto produtoExistente = repository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado para atualizar"));

        mapper.updateEntityFromDto(dto, produtoExistente);
        validator.validar(produtoExistente);
    }

    @Transactional
    public void deletar(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Para deletar é necessário que o Produto esteja salvo na base (ID não pode ser nulo).");
        }

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Produto não encontrado para deletar.");
        }

        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Produto buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Produto> buscarAtivosPorCategoria(String categoria, Pageable pageable) {
        if (categoria == null || categoria.isBlank()) {
            return buscarAtivos(pageable);
        }
        return repository.findByCategoriaAndAtivoTrue(categoria, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Produto> buscarAtivos(Pageable pageable) {
        return repository.findByAtivoTrue(pageable);
    }
}