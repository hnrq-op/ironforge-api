package br.com.henrique.estudos.ironforge_produtos.repository;

import br.com.henrique.estudos.ironforge_produtos.model.Estoque;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/dados_produtos_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EstoqueRepositoryTest {

    @Autowired
    private EstoqueRepository repository;

    @Test
    @DisplayName("findByProdutoId - Deve retornar o Estoque correspondente quando o ID do produto existir")
    void deveRetornarEstoqueQuandoIdProdutoExistir(){
        String idProduto = "123e4567-e89b-12d3-a456-426614174000";

        Optional<Estoque> estoqueOptional = repository.findByProdutoId(UUID.fromString(idProduto));

        assertTrue(estoqueOptional.isPresent());
        assertEquals(UUID.fromString(idProduto), estoqueOptional.get().getProduto().getId());
    }

    @Test
    @DisplayName("findByProdutoId - Deve retornar um optional vazio quando o ID do produto não existir")
    void deveRetornarOptionalVazioQuandoIdProdutoNaoExistir(){
        UUID idInexistente = UUID.randomUUID();

        Optional<Estoque> estoqueOptional = repository.findByProdutoId(idInexistente);

        assertTrue(estoqueOptional.isEmpty());
    }

}
