package br.com.henrique.estudos.ironforge_pedidos.client.representation.produto;

import java.util.UUID;

public record EstoqueRepresentationDTO(
        UUID id,
        Integer quantidadeDisponivel,
        Integer quantidadeReservada
) {}