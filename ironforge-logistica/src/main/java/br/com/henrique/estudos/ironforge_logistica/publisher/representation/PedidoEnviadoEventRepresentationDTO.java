package br.com.henrique.estudos.ironforge_logistica.publisher.representation;

import br.com.henrique.estudos.ironforge_logistica.model.enums.StatusEntrega;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PedidoEnviadoEventRepresentationDTO(

        UUID pedidoId,
        String codigoRastreio,
        StatusEntrega status,
        String transportadora,
        OffsetDateTime dataEnvio,
        LocalDate dataPrevisao,
        OffsetDateTime dataAtualizacao
) {
}
