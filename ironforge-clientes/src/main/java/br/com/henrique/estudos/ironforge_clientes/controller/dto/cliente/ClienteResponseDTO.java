package br.com.henrique.estudos.ironforge_clientes.controller.dto.cliente;

import br.com.henrique.estudos.ironforge_clientes.controller.dto.endereco.EnderecoResponseDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ClienteResponseDTO(
        UUID id,
        String keycloakId,
        String nome,
        String cpf,
        String email,
        String telefone,
        boolean ativo,
        OffsetDateTime dataCriacao,
        EnderecoResponseDTO endereco
) {}