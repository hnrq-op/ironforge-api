package br.com.henrique.estudos.ironforge_produtos.repository;

import br.com.henrique.estudos.ironforge_produtos.model.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/dados_produtos_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository repository;

    @Test
    @DisplayName("findBySku - Deve retornar o Produto correspondente quando o SKU existir")
    void deveRetornarProdutoQuandoSkuExistir(){
        String skuExistente = "CREATINA-500G";

        Optional<Produto> produtoOptional = repository.findBySku(skuExistente);

        assertTrue(produtoOptional.isPresent());
        assertEquals(skuExistente, produtoOptional.get().getSku());
    }

    @Test
    @DisplayName("findBySku - Deve retornar Optional vazio quando o SKU não existir")
    void deveRetornarOptionalVazioQuandoSkuNaoExistir(){
        String skuInexistente = "CREATINA-1000G";

        Optional<Produto> produtoOptional = repository.findBySku(skuInexistente);

        assertTrue(produtoOptional.isEmpty());
    }

    @Test
    @DisplayName("existBySku - Deve retornar true ao verificar existência de SKU cadastrado")
    void deveRetornarTrueQuandoSkuExistir(){
        String skuExistente = "CREATINA-500G";

        boolean existe = repository.existsBySku(skuExistente);

        assertTrue(existe);
    }

    @Test
    @DisplayName("existBySku - Deve retornar false ao verificar existência de SKU não cadastrado")
    void deveRetornarFalseQuandoSkuNaoExistir(){
        String skuInexistente = "CREATINA-1000G";

        boolean existe = repository.existsBySku(skuInexistente);

        assertFalse(existe);
    }

    @Test
    @DisplayName("findByAtivoTrue - Deve retornar apenas produtos ativos de forma paginada")
    void deveRetornarApenasProdutosAtivosPaginados() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Produto> resultado = repository.findByAtivoTrue(pageable);

        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalElements());
        assertEquals(1, resultado.getTotalPages());
        assertTrue(resultado.getContent().stream().allMatch(Produto::isAtivo));
    }

    @Test
    @DisplayName("findByCategoriaAndAtivoTrue - Deve filtrar por categoria e trazer apenas ativos")
    void deveRetornarProdutosAtivosPorCategoriaEPaginados() {
        String categoria = "Suplementos";
        Pageable pageable = PageRequest.of(0, 5);

        Page<Produto> resultado = repository.findByCategoriaAndAtivoTrue(categoria, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(categoria, resultado.getContent().get(0).getCategoria());
        assertTrue(resultado.getContent().get(0).isAtivo());
    }
}
