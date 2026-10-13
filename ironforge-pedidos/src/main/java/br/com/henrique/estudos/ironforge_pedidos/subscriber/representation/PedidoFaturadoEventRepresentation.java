package br.com.henrique.estudos.ironforge_pedidos.subscriber.representation;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PedidoFaturadoEventRepresentation(
        UUID pedidoId,
        UUID notaFiscalId,
        String numeroNota,
        String chaveAcesso,
        String pdfUrl,
        OffsetDateTime dataEmissao
) {}