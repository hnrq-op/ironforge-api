package br.com.henrique.estudos.ironforge_produtos.service;

import br.com.henrique.estudos.ironforge_produtos.controller.dto.produto.ProdutoRequestDTO;
import br.com.henrique.estudos.ironforge_produtos.exceptions.RegistroDuplicadoException;
import br.com.henrique.estudos.ironforge_produtos.mapper.ProdutoMapper;
import br.com.henrique.estudos.ironforge_produtos.model.Produto;
import br.com.henrique.estudos.ironforge_produtos.repository.ProdutoRepository;
import br.com.henrique.estudos.ironforge_produtos.validator.ProdutoValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @InjectMocks
    private ProdutoService service;

    @Mock
    private ProdutoRepository repository;

    @Mock
    private ProdutoValidator validator;

    @Mock
    private ProdutoMapper mapper;

    @Test
    @DisplayName("SALVAR - Dever salvar o produto com sucesso quando os dados forem válidos")
    void deveSalvarProdutoComSucesso(){
        Produto produto = new Produto(UUID.randomUUID(), "CREATINA-500G", "Creatina Monohidratada 500g", "IronForge", "Creatina 100% pura para treino de força", "Suplementos", BigDecimal.valueOf(119.90), true, OffsetDateTime.now());

        service.salvar(produto);

        verify(validator, times(1)).validar(produto);
        verify(repository, times(1)).save(produto);
    }

    @Test
    @DisplayName("SALVAR - Deve lançar RegistroDuplicadoException quando o validator encontrar conflito")
    void deveLancarExcecaoAoSalvarProdutoDuplicado(){
        Produto produto = new Produto(UUID.randomUUID(), "CREATINA-500G", "Creatina Monohidratada 500g", "IronForge", "Creatina 100% pura para treino de força", "Suplementos", BigDecimal.valueOf(119.90), true, OffsetDateTime.now());

        doThrow(new RegistroDuplicadoException("SKU já em uso")).when(validator).validar(produto);

        assertThrows(RegistroDuplicadoException.class, () -> service.salvar(produto));

        verify(repository, never()).save(produto);
    }

    @Test
    @DisplayName("ATUALIZAR - Deve atualizar o produto com sucesso quando o ID existir e for válido")
    void deveAtualizarProdutoComSucesso(){
        UUID id = UUID.randomUUID();
        Produto produto = new Produto(id, "CREATINA-500G", "Creatina Monohidratada 500g", "IronForge", "Creatina 100% pura para treino de força", "Suplementos", BigDecimal.valueOf(119.90), true, OffsetDateTime.now());
        ProdutoRequestDTO dto = mock(ProdutoRequestDTO.class);

        when(repository.findById(id)).thenReturn(Optional.of(produto));

        service.atualizar(id, dto);

        verify(repository).findById(id);
        verify(mapper).updateEntityFromDto(dto, produto);
        verify(validator).validar(produto);
    }

    @Test
    @DisplayName("ATUALIZAR - Deve lançar IllegalArgumentException quando o ID for nulo")
    void deveLancarExcecaoAoAtualizarComIdNulo() {
        ProdutoRequestDTO dto = mock(ProdutoRequestDTO.class);

        assertThrows(IllegalArgumentException.class, () -> service.atualizar(null, dto));

        verifyNoInteractions(repository, mapper, validator);
    }

    @Test
    @DisplayName("ATUALIZAR - Deve lançar EntityNotFoundException quando o produto não existir no banco")
    void deveLancarExcecaoAoAtualizarProdutoInexistente() {
        UUID id = UUID.randomUUID();
        ProdutoRequestDTO dto = mock(ProdutoRequestDTO.class);

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.atualizar(id, dto));

        verify(repository).findById(id);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("DELETAR - Deve deletar o produto com sucesso quando o ID existir")
    void deveDeletarProdutoComSucesso(){
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.deletar(id);

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("DELETAR - Deve lançar IllegalArgumentException quando o ID passado for nulo")
    void deveLancarExcecaoAoDeletarComIdNulo(){
        assertThrows(IllegalArgumentException.class, () -> service.deletar(null));
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("DELETAR - Deve lançar EntityNotFoundException quando o ID do usuário não existir")
    void deveLancarExcecaoAoDeletarIdInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.deletar(id));

        verify(repository).existsById(id);
        verify(repository, never()).deleteById(id);
    }

    @Test
    @DisplayName("buscarPorId - Deve retornar Optional preenchido quando o id existir")
    void deveRetornarOptionalComProdutoQuandoIdExistir() {
        UUID id = UUID.randomUUID();
        Produto produtoEsperado = new Produto(id, "CREATINA-500G", "Creatina Monohidratada 500g", "IronForge", "Creatina 100% pura para treino de força", "Suplementos", BigDecimal.valueOf(119.90), true, OffsetDateTime.now());

        when(repository.findById(id)).thenReturn(Optional.of(produtoEsperado));

        Produto produtoOptional = service.buscarPorId(id);

        assertNotNull(produtoOptional);
        assertEquals(id, produtoOptional.getId());

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("buscarAtivosPorCategoria - Deve retornar página filtrada por categoria quando a categoria for informada")
    void deveBuscarAtivosPorCategoriaQuandoCategoriaForInformada() {
        String categoria = "Suplementos";
        Pageable pageable = PageRequest.of(0, 10);
        Produto produto = new Produto(UUID.randomUUID(), "CREATINA-500G", "Creatina Monohidratada 500g", "IronForge", "Creatina 100% pura para treino de força", categoria, BigDecimal.valueOf(119.90), true, OffsetDateTime.now());
        Page<Produto> pageEsperada = new PageImpl<>(List.of(produto));

        when(repository.findByCategoriaAndAtivoTrue(categoria, pageable)).thenReturn(pageEsperada);

        Page<Produto> resultado = service.buscarAtivosPorCategoria(categoria, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(produto, resultado.getContent().get(0));

        verify(repository).findByCategoriaAndAtivoTrue(categoria, pageable);
        verify(repository, never()).findByAtivoTrue(any());
    }

    @Test
    @DisplayName("buscarAtivosPorCategoria - Deve buscar todos os ativos quando a categoria for nula ou em branco")
    void deveBuscarTodosAtivosQuandoCategoriaForNulaOuEmBranco() {
        Pageable pageable = PageRequest.of(0, 10);
        Produto produto = new Produto(UUID.randomUUID(), "CREATINA-500G", "Creatina Monohidratada 500g", "IronForge", "Creatina 100% pura para treino de força", "Suplementos", BigDecimal.valueOf(119.90), true, OffsetDateTime.now());
        Page<Produto> pageEsperada = new PageImpl<>(List.of(produto));

        when(repository.findByAtivoTrue(pageable)).thenReturn(pageEsperada);

        Page<Produto> resultadoNull = service.buscarAtivosPorCategoria(null, pageable);
        Page<Produto> resultadoBlank = service.buscarAtivosPorCategoria("   ", pageable);

        assertNotNull(resultadoNull);
        assertNotNull(resultadoBlank);
        assertEquals(1, resultadoNull.getTotalElements());
        assertEquals(1, resultadoBlank.getTotalElements());

        verify(repository, times(2)).findByAtivoTrue(pageable);
        verify(repository, never()).findByCategoriaAndAtivoTrue(anyString(), any());
    }

    @Test
    @DisplayName("buscarAtivos - Deve retornar página de produtos ativos com sucesso")
    void deveBuscarAtivosComSucesso() {
        Pageable pageable = PageRequest.of(0, 10);
        Produto produto = new Produto(UUID.randomUUID(), "CREATINA-500G", "Creatina Monohidratada 500g", "IronForge", "Creatina 100% pura para treino de força", "Suplementos", BigDecimal.valueOf(119.90), true, OffsetDateTime.now());
        Page<Produto> pageEsperada = new PageImpl<>(List.of(produto));

        when(repository.findByAtivoTrue(pageable)).thenReturn(pageEsperada);

        Page<Produto> resultado = service.buscarAtivos(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(produto, resultado.getContent().get(0));

        verify(repository).findByAtivoTrue(pageable);
    }
}
