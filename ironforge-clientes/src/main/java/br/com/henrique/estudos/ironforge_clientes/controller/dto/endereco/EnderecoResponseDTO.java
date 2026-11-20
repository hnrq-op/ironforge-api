package br.com.henrique.estudos.ironforge_clientes.controller.dto.endereco;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados de retorno de um endereço")
public record EnderecoResponseDTO(

        @Schema(description = "Identificador único (UUID) do endereço", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome da rua, avenida ou logradouro", example = "Avenida Brasil")
        String logradouro,

        @Schema(description = "Número do imóvel", example = "450")
        String numero,

        @Schema(description = "Complemento do endereço", example = "Apto 202")
        String complemento,

        @Schema(description = "Nome do bairro", example = "Centro")
        String bairro,

        @Schema(description = "Nome do município", example = "Buriti Alegre")
        String cidade,

        @Schema(description = "Sigla da Unidade Federativa (UF)", example = "GO")
        String estado,

        @Schema(description = "Código de Endereçamento Postal (CEP)", example = "75660-000")
        String cep

) {}