package br.com.henrique.estudos.ironforge_pedidos.client.representation.cliente;

import java.util.UUID;

public record EnderecoRepresentationDTO(
        UUID id,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep
) {}