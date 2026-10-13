package br.com.henrique.estudos.ironforge_logistica.controller.dto;

import br.com.henrique.estudos.ironforge_logistica.model.enums.StatusEntrega;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record RecebimentoCallbackLogisticaDTO(

        @NotNull(message = "O ID da entrega é obrigatório")
        UUID id,

        @NotNull(message = "O ID do pedido é obrigatório")
        UUID pedidoId,

        @NotBlank(message = "A chave de logística é obrigatória para autenticação do webhook")
        String chaveLogistica,

        @NotBlank(message = "O código de rastreio é obrigatório")
        String codigoRastreio,

        @NotNull(message = "O novo status da entrega é obrigatório")
        StatusEntrega status,

        @NotBlank(message = "A transportadora é obrigatória")
        String transportadora,

        @NotNull(message = "A data de previsão é obrigatória")
        LocalDate dataPrevisao

) {}