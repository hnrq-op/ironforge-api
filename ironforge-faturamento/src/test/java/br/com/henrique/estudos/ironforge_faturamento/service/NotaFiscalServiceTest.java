package br.com.henrique.estudos.ironforge_faturamento.service;

import br.com.henrique.estudos.ironforge_faturamento.subscriber.representation.ClienteEventRepresentationDTO;
import br.com.henrique.estudos.ironforge_faturamento.subscriber.representation.EnderecoEventRepresentationDTO;
import br.com.henrique.estudos.ironforge_faturamento.subscriber.representation.ItemPedidoEventRepresentationDTO;
import br.com.henrique.estudos.ironforge_faturamento.subscriber.representation.PedidoPagoEventRepresentationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotaFiscalServiceTest {

    @Mock
    private Resource notaFiscalResource;

    @Mock
    private Resource logoResource;

    @InjectMocks
    private NotaFiscalService notaFiscalService;

    private static final String JRXML_MINIMO = """
            <?xml version="1.0" encoding="UTF-8"?>
            <jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports"
                          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                          xsi:schemaLocation="http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd"
                          name="nota_fiscal" pageWidth="595" pageHeight="842" columnWidth="555"
                          leftMargin="20" rightMargin="20" topMargin="20" bottomMargin="20">
                <parameter name="NUMERO_NOTA" class="java.lang.String"/>
                <parameter name="CHAVE_ACESSO" class="java.lang.String"/>
                <parameter name="NOME" class="java.lang.String"/>
                <parameter name="CPF" class="java.lang.String"/>
                <parameter name="EMAIL" class="java.lang.String"/>
                <parameter name="TELEFONE" class="java.lang.String"/>
                <parameter name="LOGRADOURO" class="java.lang.String"/>
                <parameter name="NUMERO" class="java.lang.String"/>
                <parameter name="BAIRRO" class="java.lang.String"/>
                <parameter name="DATA_PEDIDO" class="java.lang.String"/>
                <parameter name="TOTAL_PEDIDO" class="java.math.BigDecimal"/>
                <parameter name="LOGO" class="java.lang.String"/>
                <field name="sku" class="java.lang.String"/>
                <field name="nome" class="java.lang.String"/>
                <field name="quantidade" class="java.lang.Integer"/>
                <field name="precoUnitario" class="java.math.BigDecimal"/>
                <field name="subtotal" class="java.math.BigDecimal"/>
                <detail>
                    <band height="20">
                        <textField>
                            <reportElement x="0" y="0" width="100" height="20"/>
                            <textFieldExpression><![CDATA[$F{nome}]]></textFieldExpression>
                        </textField>
                    </band>
                </detail>
            </jasperReport>
            """;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notaFiscalService, "notaFiscalResource", notaFiscalResource);
        ReflectionTestUtils.setField(notaFiscalService, "logoResource", logoResource);
    }

    @Test
    @DisplayName("Deve gerar nota fiscal em PDF com sucesso quando todos os dados estiverem preenchidos")
    void deveGerarNotaFiscalComSucessoQuandoDadosForemCompletos() throws Exception {
        when(notaFiscalResource.getInputStream()).thenReturn(new ByteArrayInputStream(JRXML_MINIMO.getBytes()));
        when(logoResource.exists()).thenReturn(true);
        when(logoResource.getURL()).thenReturn(new URL("http://localhost/logo.png"));

        EnderecoEventRepresentationDTO endereco = new EnderecoEventRepresentationDTO(
                "Rua A", "123", "Apto 1", "Centro", "Goiânia", "GO", "74000-000"
        );
        ClienteEventRepresentationDTO cliente = new ClienteEventRepresentationDTO(
                UUID.randomUUID(), "Henrique Pires", "12345678900", "henrique@test.com", "62999999999", endereco
        );
        ItemPedidoEventRepresentationDTO item = new ItemPedidoEventRepresentationDTO(
                UUID.randomUUID(), "SKU-123", "Teclado Mecânico", 1, new BigDecimal("250.00"), new BigDecimal("250.00")
        );
        PedidoPagoEventRepresentationDTO pedido = new PedidoPagoEventRepresentationDTO(
                UUID.randomUUID(), "PAGO", new BigDecimal("250.00"), "PAY-1", LocalDateTime.now(), cliente, List.of(item)
        );

        byte[] pdfBytes = notaFiscalService.gerarNota(pedido, "000000001", "CHAVE123456");

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
    }

    @Test
    @DisplayName("Deve usar o produtoId como fallback no relatório quando o SKU do item for nulo")
    void deveUsarProdutoIdComoSkuQuandoSkuForNulo() throws Exception {
        when(notaFiscalResource.getInputStream()).thenReturn(new ByteArrayInputStream(JRXML_MINIMO.getBytes()));
        when(logoResource.exists()).thenReturn(false);

        UUID produtoId = UUID.randomUUID();
        ItemPedidoEventRepresentationDTO itemSemSku = new ItemPedidoEventRepresentationDTO(
                produtoId, null, "Mouse Gamer", 2, new BigDecimal("100.00"), new BigDecimal("200.00")
        );
        PedidoPagoEventRepresentationDTO pedido = new PedidoPagoEventRepresentationDTO(
                UUID.randomUUID(), "PAGO", new BigDecimal("200.00"), "PAY-2", LocalDateTime.now(), null, List.of(itemSemSku)
        );

        byte[] pdfBytes = notaFiscalService.gerarNota(pedido, "000000002", "CHAVE654321");

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
    }

    @Test
    @DisplayName("Deve gerar nota fiscal com sucesso quando o cliente for nulo")
    void deveGerarNotaFiscalComSucessoQuandoClienteForNulo() throws Exception {
        when(notaFiscalResource.getInputStream()).thenReturn(new ByteArrayInputStream(JRXML_MINIMO.getBytes()));
        when(logoResource.exists()).thenReturn(false);

        PedidoPagoEventRepresentationDTO pedidoSemCliente = new PedidoPagoEventRepresentationDTO(
                UUID.randomUUID(), "PAGO", new BigDecimal("100.00"), "PAY-3", LocalDateTime.now(), null, List.of()
        );

        byte[] pdfBytes = notaFiscalService.gerarNota(pedidoSemCliente, "000000003", "CHAVE000");

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
    }

    @Test
    @DisplayName("Deve gerar nota fiscal com sucesso quando o endereço de entrega do cliente for nulo")
    void deveGerarNotaFiscalComSucessoQuandoEnderecoEntregaForNulo() throws Exception {
        when(notaFiscalResource.getInputStream()).thenReturn(new ByteArrayInputStream(JRXML_MINIMO.getBytes()));
        when(logoResource.exists()).thenReturn(false);

        ClienteEventRepresentationDTO clienteSemEndereco = new ClienteEventRepresentationDTO(
                UUID.randomUUID(), "Henrique", "12345678900", "h@test.com", "62999999999", null
        );
        PedidoPagoEventRepresentationDTO pedido = new PedidoPagoEventRepresentationDTO(
                UUID.randomUUID(), "PAGO", new BigDecimal("100.00"), "PAY-4", LocalDateTime.now(), clienteSemEndereco, List.of()
        );

        byte[] pdfBytes = notaFiscalService.gerarNota(pedido, "000000004", "CHAVE111");

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
    }

    @Test
    @DisplayName("Deve gerar nota fiscal com sucesso quando a data de pagamento do pedido for nula")
    void deveGerarNotaFiscalComSucessoQuandoDataPagamentoForNula() throws Exception {
        when(notaFiscalResource.getInputStream()).thenReturn(new ByteArrayInputStream(JRXML_MINIMO.getBytes()));
        when(logoResource.exists()).thenReturn(false);

        PedidoPagoEventRepresentationDTO pedidoSemData = new PedidoPagoEventRepresentationDTO(
                UUID.randomUUID(), "PAGO", new BigDecimal("100.00"), "PAY-5", null, null, List.of()
        );

        byte[] pdfBytes = notaFiscalService.gerarNota(pedidoSemData, "000000005", "CHAVE222");

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
    }

    @Test
    @DisplayName("Deve gerar nota fiscal sem a logo quando o arquivo de logo não existir")
    void deveGerarNotaFiscalSemLogoQuandoLogoNaoExistir() throws Exception {
        when(notaFiscalResource.getInputStream()).thenReturn(new ByteArrayInputStream(JRXML_MINIMO.getBytes()));
        when(logoResource.exists()).thenReturn(false);

        PedidoPagoEventRepresentationDTO pedido = new PedidoPagoEventRepresentationDTO(
                UUID.randomUUID(), "PAGO", new BigDecimal("100.00"), "PAY-6", LocalDateTime.now(), null, List.of()
        );

        byte[] pdfBytes = notaFiscalService.gerarNota(pedido, "000000006", "CHAVE333");

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando ocorrer erro ao ler o recurso JRXML")
    void deveLancarExcecaoQuandoOcorrerErroNaLeituraDoResource() throws Exception {
        when(notaFiscalResource.getInputStream()).thenThrow(new IOException("Arquivo JRXML não encontrado"));

        PedidoPagoEventRepresentationDTO pedido = new PedidoPagoEventRepresentationDTO(
                UUID.randomUUID(), "PAGO", new BigDecimal("100.00"), "PAY-7", LocalDateTime.now(), null, List.of()
        );

        assertThatThrownBy(() -> notaFiscalService.gerarNota(pedido, "000000007", "CHAVEERR"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Erro ao gerar o relatório em PDF da nota fiscal")
                .hasCauseInstanceOf(IOException.class);
    }
}