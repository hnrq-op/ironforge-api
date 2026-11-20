package br.com.henrique.estudos.ironforge_clientes.controller.dto.endereco;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados de entrada para cadastro ou atualização de endereço")
public record EnderecoRequestDTO(

        @Schema(description = "Nome da rua, avenida ou logradouro", example = "Avenida Brasil")
        @NotBlank(message = "O logradouro é obrigatório")
        @Size(max = 255, message = "O logradouro não pode exceder 255 caracteres")
        String logradouro,

        @Schema(description = "Número do imóvel", example = "450")
        @NotBlank(message = "O número é obrigatório")
        @Size(max = 20, message = "O número não pode exceder 20 caracteres")
        String numero,

        @Schema(description = "Complemento opcional (apartamento, bloco, sala)", example = "Apto 202")
        @Size(max = 100, message = "O complemento não pode exceder 100 caracteres")
        String complemento,

        @Schema(description = "Nome do bairro", example = "Centro")
        @NotBlank(message = "O bairro é obrigatório")
        @Size(max = 100, message = "O bairro não pode exceder 100 caracteres")
        String bairro,

        @Schema(description = "Nome do município", example = "Buriti Alegre")
        @NotBlank(message = "A cidade é obrigatória")
        @Size(max = 100, message = "A cidade não pode exceder 100 caracteres")
        String cidade,

        @Schema(description = "Sigla da Unidade Federativa (UF) com 2 letras", example = "GO")
        @NotBlank(message = "O estado é obrigatório")
        @Size(min = 2, max = 2, message = "O estado deve ter exatamente 2 caracteres (UF)")
        String estado,

        @Schema(description = "Código de Endereçamento Postal (12345-678 ou 12345678)", example = "75660-000")
        @NotBlank(message = "O CEP é obrigatório")
        @Pattern(regexp = "\\d{5}-\\d{3}|\\d{8}", message = "O CEP deve estar no formato 12345-678 ou 12345678")
        String cep

) {}