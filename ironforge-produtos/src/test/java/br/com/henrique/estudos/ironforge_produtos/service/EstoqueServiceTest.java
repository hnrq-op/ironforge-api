package br.com.henrique.estudos.ironforge_produtos.service;

import br.com.henrique.estudos.ironforge_produtos.controller.dto.estoque.EstoqueRequestDTO;
import br.com.henrique.estudos.ironforge_produtos.exceptions.RegistroDuplicadoException;
import br.com.henrique.estudos.ironforge_produtos.mapper.EstoqueMapper;
import br.com.henrique.estudos.ironforge_produtos.model.Estoque;
import br.com.henrique.estudos.ironforge_produtos.model.Produto;
import br.com.henrique.estudos.ironforge_produtos.repository.EstoqueRepository;
import br.com.henrique.estudos.ironforge_produtos.validator.EstoqueValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstoqueServiceTest {

    @InjectMocks
    private EstoqueService service;

    @Mock
    private EstoqueRepository repository;

    @Mock
    private EstoqueValidator validator;

    @Mock
    private EstoqueMapper mapper;

    @Test
    @DisplayName("SALVAR - Deve salvar o estoque com sucesso quando os dados forem válidos")
    void deveSalvarEstoqueComSucesso() {
        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        Estoque estoque = new Estoque(UUID.randomUUID(), 100, 10, OffsetDateTime.now(), produto);
        service.salvar(estoque);

        verify(validator, times(1)).validar(estoque);
        verify(repository, times(1)).save(estoque);
        assertNotNull(estoque.getDataAtualizacao());
    }

    @Test
    @DisplayName("SALVAR - Deve lançar RegistroDuplicadoException quando o validator encontrar conflito")
    void deveLancarExcecaoAoSalvarEstoqueDuplicado() {
        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        Estoque estoque = new Estoque(UUID.randomUUID(), 100, 10, OffsetDateTime.now(), produto);

        doThrow(new RegistroDuplicadoException("Já existe estoque cadastrado para este produto"))
                .when(validator).validar(estoque);

        assertThrows(RegistroDuplicadoException.class, () -> service.salvar(estoque));

        verify(repository, never()).save(estoque);
    }

    @Test
    @DisplayName("ATUALIZAR - Deve atualizar o estoque com sucesso quando o ID existir e for válido")
    void deveAtualizarEstoqueComSucesso() {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        Estoque estoque = new Estoque(UUID.randomUUID(), 100, 10, OffsetDateTime.now(), produto);
        EstoqueRequestDTO dto = mock(EstoqueRequestDTO.class);

        when(repository.findById(id)).thenReturn(Optional.of(estoque));

        service.atualizar(id, dto);

        verify(repository).findById(id);
        verify(mapper).updateEntityFromDto(dto, estoque);
        verify(validator).validar(estoque);
    }

    @Test
    @DisplayName("ATUALIZAR - Deve lançar IllegalArgumentException quando o ID for nulo")
    void deveLancarExcecaoAoAtualizarComIdNulo() {
        EstoqueRequestDTO dto = mock(EstoqueRequestDTO.class);

        assertThrows(IllegalArgumentException.class, () -> service.atualizar(null, dto));

        verifyNoInteractions(repository, mapper, validator);
    }

    @Test
    @DisplayName("ATUALIZAR - Deve lançar EntityNotFoundException quando o estoque não existir no banco")
    void deveLancarExcecaoAoAtualizarEstoqueInexistente() {
        UUID id = UUID.randomUUID();
        EstoqueRequestDTO dto = mock(EstoqueRequestDTO.class);

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.atualizar(id, dto));

        verify(repository).findById(id);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("DELETAR - Deve deletar o estoque com sucesso quando o ID existir")
    void deveDeletarEstoqueComSucesso() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.deletar(id);

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("DELETAR - Deve lançar IllegalArgumentException quando o ID passado for nulo")
    void deveLancarExcecaoAoDeletarComIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.deletar(null));
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("DELETAR - Deve lançar EntityNotFoundException quando o ID não existir")
    void deveLancarExcecaoAoDeletarIdInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.deletar(id));

        verify(repository).existsById(id);
        verify(repository, never()).deleteById(id);
    }

    @Test
    @DisplayName("buscarPorId - Deve retornar estoque quando o ID existir")
    void deveRetornarEstoqueQuandoIdExistir() {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto();
        Estoque estoqueEsperado = new Estoque(id, 100, 10, OffsetDateTime.now(), produto);

        when(repository.findById(id)).thenReturn(Optional.of(estoqueEsperado));

        Estoque estoqueRetornado = service.buscarPorId(id);

        assertNotNull(estoqueRetornado);
        assertEquals(id, estoqueRetornado.getId());

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("buscarPorId - Deve lançar EntityNotFoundException quando o ID não existir")
    void deveLancarExcecaoAoBuscarPorIdInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.buscarPorId(id));

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("buscarPorProdutoId - Deve retornar estoque quando o ID do produto existir")
    void deveRetornarEstoqueQuandoProdutoIdExistir() {
        UUID produtoId = UUID.randomUUID();
        Produto produto = new Produto();
        produto.setId(produtoId);
        Estoque estoqueEsperado = new Estoque(UUID.randomUUID(), 100, 10, OffsetDateTime.now(), produto);

        when(repository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoqueEsperado));

        Estoque estoqueRetornado = service.buscarPorProdutoId(produtoId);

        assertNotNull(estoqueRetornado);
        assertEquals(produtoId, estoqueRetornado.getProduto().getId());

        verify(repository).findByProdutoId(produtoId);
    }

    @Test
    @DisplayName("buscarPorProdutoId - Deve lançar EntityNotFoundException quando não existir estoque para o produto")
    void deveLancarExcecaoAoBuscarPorProdutoIdInexistente() {
        UUID produtoId = UUID.randomUUID();
        when(repository.findByProdutoId(produtoId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.buscarPorProdutoId(produtoId));

        verify(repository).findByProdutoId(produtoId);
    }
}