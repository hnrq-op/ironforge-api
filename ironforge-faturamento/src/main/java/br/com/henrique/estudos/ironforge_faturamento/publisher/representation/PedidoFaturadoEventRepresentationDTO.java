package br.com.henrique.estudos.ironforge_faturamento.publisher.representation;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PedidoFaturadoEventRepresentationDTO(
        UUID pedidoId,
        UUID notaFiscalId,
        String numeroNota,
        String chaveAcesso,
        String pdfUrl,
        OffsetDateTime dataEmissao
) {}