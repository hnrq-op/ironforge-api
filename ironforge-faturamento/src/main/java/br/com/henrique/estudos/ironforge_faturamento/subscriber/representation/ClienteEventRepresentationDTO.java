package br.com.henrique.estudos.ironforge_faturamento.subscriber.representation;

import java.util.UUID;

public record ClienteEventRepresentationDTO(
        UUID clienteId,
        String nome,
        String cpf,
        String email,
        String telefone,
        EnderecoEventRepresentationDTO enderecoEntrega
) {}