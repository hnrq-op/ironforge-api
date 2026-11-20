package br.com.henrique.estudos.ironforge_clientes.controller.dto.cliente;

import br.com.henrique.estudos.ironforge_clientes.controller.dto.endereco.EnderecoResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Dados completos de retorno de um cliente")
public record ClienteResponseDTO(

        @Schema(description = "Identificador único (UUID) do cliente na base", example = "7d444840-9dc0-11d1-b245-5ffdce74fad2")
        UUID id,

        @Schema(description = "Identificador único (subject) do usuário no Keycloak", example = "a81bc81b-dead-4e5d-abff-90865d1e13b1")
        String keycloakId,

        @Schema(description = "Nome completo do cliente", example = "Henrique Oliveira Pires")
        String nome,

        @Schema(description = "CPF do cliente", example = "52998224725")
        String cpf,

        @Schema(description = "E-mail cadastrado do cliente", example = "henrique.pires@email.com")
        String email,

        @Schema(description = "Telefone de contato", example = "64999887766")
        String telefone,

        @Schema(description = "Indica se o cadastro do cliente está ativo", example = "true")
        boolean ativo,

        @Schema(description = "Data e hora de criação do cadastro", example = "2026-11-20T11:00:00-03:00")
        OffsetDateTime dataCriacao,

        @Schema(description = "Endereço principal associado ao cliente")
        EnderecoResponseDTO endereco

) {}