package br.com.henrique.estudos.ironforge_pedidos.client.representation.cliente;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Representação dos dados cadastrais do cliente retornados junto aos detalhes do pedido")
public record ClienteRepresentationDTO(

        @Schema(description = "Identificador único do cliente", example = "7d444840-9dc0-11d1-b245-5ffdce74fad2")
        UUID id,

        @Schema(description = "Nome completo do cliente", example = "Henrique Oliveira Pires")
        String nome,

        @Schema(description = "CPF do cliente", example = "12345678901")
        String cpf,

        @Schema(description = "E-mail de contato do cliente", example = "henrique@email.com")
        String email,

        @Schema(description = "Telefone de contato do cliente", example = "64999999999")
        String telefone,

        @Schema(description = "Indica se o cadastro do cliente está ativo", example = "true")
        boolean ativo,

        @Schema(description = "Endereço principal de entrega do cliente")
        EnderecoRepresentationDTO endereco

) {}