package br.com.henrique.estudos.ironforge_pedidos.subscriber.representation;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PedidoEnviadoEventRepresentation(
        UUID pedidoId,
        String codigoRastreio,
        String status,
        String transportadora,
        OffsetDateTime dataEnvio,
        LocalDate dataPrevisao,
        OffsetDateTime dataAtualizacao
) {}