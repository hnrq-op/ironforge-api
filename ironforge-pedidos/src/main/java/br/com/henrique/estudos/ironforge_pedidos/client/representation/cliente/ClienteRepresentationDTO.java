package br.com.henrique.estudos.ironforge_pedidos.client.representation.cliente;

import java.util.List;
import java.util.UUID;

public record ClienteRepresentationDTO(
        UUID id,
        String nome,
        String cpf,
        String email,
        String telefone,
        boolean ativo,
        EnderecoRepresentationDTO endereco
) {}