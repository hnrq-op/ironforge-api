package br.com.henrique.estudos.ironforge_logistica.controller.dto;

import br.com.henrique.estudos.ironforge_logistica.model.enums.StatusEntrega;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Payload enviado pela transportadora externa para notificar atualizações na entrega")
public record RecebimentoCallbackLogisticaDTO(

        @Schema(description = "Identificador único (UUID) do registro de entrega", example = "4a7b8c9d-1e2f-3a4b-5c6d-7e8f9a0b1c2d")
        @NotNull(message = "O ID da entrega é obrigatório")
        UUID id,

        @Schema(description = "Identificador único (UUID) do pedido vinculado à entrega", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
        @NotNull(message = "O ID do pedido é obrigatório")
        UUID pedidoId,

        @Schema(description = "Chave de autenticação logística vinculada à entrega", example = "log-key-9876543210abcdef")
        @NotBlank(message = "A chave de logística é obrigatória para autenticação do webhook")
        String chaveLogistica,

        @Schema(description = "Código de rastreamento gerado pela transportadora", example = "BR123456789IF")
        @NotBlank(message = "O código de rastreio é obrigatório")
        String codigoRastreio,

        @Schema(description = "Novo status da entrega reportado pela transportadora", example = "ENVIADO")
        @NotNull(message = "O novo status da entrega é obrigatório")
        StatusEntrega status,

        @Schema(description = "Nome da transportadora responsável pelo transporte", example = "Jadlog")
        @NotBlank(message = "A transportadora é obrigatória")
        String transportadora,

        @Schema(description = "Data prevista para entrega ao destinatário", example = "2026-11-25")
        @NotNull(message = "A data de previsão é obrigatória")
        LocalDate dataPrevisao

) {}