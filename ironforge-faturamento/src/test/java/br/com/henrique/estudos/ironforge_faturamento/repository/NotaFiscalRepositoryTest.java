package br.com.henrique.estudos.ironforge_faturamento.repository;

import br.com.henrique.estudos.ironforge_faturamento.model.NotaFiscal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/dados_nota_fiscal_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class NotaFiscalRepositoryTest {

    @Autowired
    private NotaFiscalRepository notaFiscalRepository;

    private final UUID pedidoIdExistente = UUID.fromString("c3d4e5f6-7a8b-9c0d-1e2f-3a4b5c6d7e8f");
    private final UUID pedidoIdInexistente = UUID.fromString("11111111-2222-3333-4444-555555555555");

    @Test
    @DisplayName("existsByPedidoId - Deve retornar true quando existir Nota Fiscal cadastrada para o pedidoId")
    void deveRetornarTrueQuandoExisteNotaFiscalParaPedidoId() {
        boolean existe = notaFiscalRepository.existsByPedidoId(pedidoIdExistente);

        assertTrue(existe);
    }

    @Test
    @DisplayName("existsByPedidoId - Deve retornar false quando não existir Nota Fiscal cadastrada para o pedidoId")
    void deveRetornarFalseQuandoNaoExisteNotaFiscalParaPedidoId() {
        boolean existe = notaFiscalRepository.existsByPedidoId(pedidoIdInexistente);

        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("save - Deve salvar uma nova Nota Fiscal no banco de dados com sucesso")
    void deveSalvarNotaFiscalComSucesso() {
        UUID novoPedidoId = UUID.randomUUID();
        NotaFiscal novaNota = NotaFiscal.builder()
                .pedidoId(novoPedidoId)
                .numeroNota("000000002")
                .chaveAcesso("35261012345678000195550010000000011000000002")
                .pdfUrl("http://localhost:9000/ironforge-faturas/notafiscal_pedido_2.pdf")
                .dataEmissao(OffsetDateTime.now())
                .build();

        NotaFiscal notaSalva = notaFiscalRepository.save(novaNota);

        assertThat(notaSalva).isNotNull();
        assertThat(notaSalva.getId()).isNotNull();
        assertThat(notaSalva.getPedidoId()).isEqualTo(novoPedidoId);

        boolean existeNoBanco = notaFiscalRepository.existsByPedidoId(novoPedidoId);
        assertThat(existeNoBanco).isTrue();
    }
}
