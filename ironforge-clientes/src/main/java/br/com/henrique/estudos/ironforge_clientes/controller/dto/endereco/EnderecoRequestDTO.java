package br.com.henrique.estudos.ironforge_clientes.controller.dto.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoRequestDTO(

        @NotBlank(message = "O logradouro é obrigatório")
        @Size(max = 255, message = "O logradouro não pode exceder 255 caracteres")
        String logradouro,

        @NotBlank(message = "O número é obrigatório")
        @Size(max = 20, message = "O número não pode exceder 20 caracteres")
        String numero,

        @Size(max = 100, message = "O complemento não pode exceder 100 caracteres")
        String complemento,

        @NotBlank(message = "O bairro é obrigatório")
        @Size(max = 100, message = "O bairro não pode exceder 100 caracteres")
        String bairro,

        @NotBlank(message = "A cidade é obrigatória")
        @Size(max = 100, message = "A cidade não pode exceder 100 caracteres")
        String cidade,

        @NotBlank(message = "O estado é obrigatório")
        @Size(min = 2, max = 2, message = "O estado deve ter exatamente 2 caracteres (UF)")
        String estado,

        @NotBlank(message = "O CEP é obrigatório")
        @Pattern(regexp = "\\d{5}-\\d{3}|\\d{8}", message = "O CEP deve estar no formato 12345-678 ou 12345678")
        String cep
) {}