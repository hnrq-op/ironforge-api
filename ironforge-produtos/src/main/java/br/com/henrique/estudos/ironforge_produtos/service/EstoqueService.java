package br.com.henrique.estudos.ironforge_produtos.service;

import br.com.henrique.estudos.ironforge_produtos.controller.dto.estoque.EstoqueRequestDTO;
import br.com.henrique.estudos.ironforge_produtos.mapper.EstoqueMapper;
import br.com.henrique.estudos.ironforge_produtos.model.Estoque;
import br.com.henrique.estudos.ironforge_produtos.repository.EstoqueRepository;
import br.com.henrique.estudos.ironforge_produtos.validator.EstoqueValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository repository;
    private final EstoqueValidator validator;
    private final EstoqueMapper mapper;

    @Transactional
    public void salvar(Estoque estoque) {
        estoque.setDataAtualizacao(OffsetDateTime.now());
        validator.validar(estoque);
        repository.save(estoque);
    }

    @Transactional
    public void atualizar(UUID id, EstoqueRequestDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("Para atualizar é necessário que o estoque esteja salvo na base (ID não pode ser nulo).");
        }

        Estoque estoqueExistente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado para atualizar."));

        mapper.updateEntityFromDto(dto, estoqueExistente);
        estoqueExistente.setDataAtualizacao(OffsetDateTime.now());
        validator.validar(estoqueExistente);
    }

    @Transactional
    public void deletar(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Para deletar é necessário que o Estoque esteja salvo na base (ID não pode ser nulo).");
        }

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Estoque não encontrado para deletar.");
        }

        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Estoque buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public Estoque buscarPorProdutoId(UUID produtoId) {
        return repository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado para o produto ID: " + produtoId));
    }
}