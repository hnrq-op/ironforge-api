package br.com.henrique.estudos.ironforge_faturamento.service;

import br.com.henrique.estudos.ironforge_faturamento.service.dto.ItemNotaFiscalReportDTO;
import br.com.henrique.estudos.ironforge_faturamento.subscriber.representation.PedidoPagoEventRepresentationDTO;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotaFiscalService {

    @Value("classpath:reports/nota-fiscal.jrxml")
    private Resource notaFiscalResource;

    @Value("classpath:reports/logo.png")
    private Resource logoResource;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] gerarNota(PedidoPagoEventRepresentationDTO pedido, String numeroNota, String chaveAcesso) {
        try (InputStream inputStream = notaFiscalResource.getInputStream()) {

            Map<String, Object> params = new HashMap<>();
            params.put("NUMERO_NOTA", numeroNota);
            params.put("CHAVE_ACESSO", chaveAcesso);

            if (pedido.cliente() != null) {
                params.put("NOME", pedido.cliente().nome());
                params.put("CPF", pedido.cliente().cpf());
                params.put("EMAIL", pedido.cliente().email());
                params.put("TELEFONE", pedido.cliente().telefone());

                if (pedido.cliente().enderecoEntrega() != null) {
                    params.put("LOGRADOURO", pedido.cliente().enderecoEntrega().logradouro());
                    params.put("NUMERO", pedido.cliente().enderecoEntrega().numero());
                    params.put("BAIRRO", pedido.cliente().enderecoEntrega().bairro());
                }
            }

            String dataFormatada = pedido.dataPagamento() != null
                    ? pedido.dataPagamento().format(DATE_FORMATTER)
                    : "";

            params.put("DATA_PEDIDO", dataFormatada);
            params.put("TOTAL_PEDIDO", pedido.valorTotal());

            if (logoResource.exists()) {
                params.put("LOGO", logoResource.getURL().toExternalForm());
            }

            List<ItemNotaFiscalReportDTO> itensReport = pedido.itens().stream()
                    .map(item -> new ItemNotaFiscalReportDTO(
                            item.sku() != null ? item.sku() : item.produtoId().toString(),
                            item.nome(),
                            item.quantidade(),
                            item.precoUnitario(),
                            item.subtotal()
                    ))
                    .toList();

            var dataSource = new JRBeanCollectionDataSource(itensReport);

            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar o relatório em PDF da nota fiscal", e);
        }
    }
}