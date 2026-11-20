package br.com.henrique.estudos.ironforge_clientes.controller.dto.cliente;

import br.com.henrique.estudos.ironforge_clientes.controller.dto.endereco.EnderecoRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

@Schema(description = "Dados de entrada para cadastro ou atualização de cliente")
public record ClienteRequestDTO(

        @Schema(description = "Nome completo do cliente", example = "Henrique Oliveira Pires")
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 2, max = 255, message = "O nome deve ter entre 2 e 255 caracteres")
        String nome,

        @Schema(description = "CPF válido do cliente (apenas dígitos ou formatado)", example = "52998224725")
        @NotBlank(message = "O CPF é obrigatório")
        @CPF(message = "O CPF informado é inválido")
        String cpf,

        @Schema(description = "Endereço de e-mail único do cliente", example = "henrique.pires@email.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail informado é inválido")
        String email,

        @Schema(description = "Telefone de contato com DDD", example = "64999887766")
        @Size(max = 20, message = "O telefone não pode exceder 20 caracteres")
        String telefone,

        @Schema(description = "Endereço principal vinculado ao cliente")
        @NotNull(message = "Os dados de endereço são obrigatórios")
        @Valid
        EnderecoRequestDTO endereco

) {
}