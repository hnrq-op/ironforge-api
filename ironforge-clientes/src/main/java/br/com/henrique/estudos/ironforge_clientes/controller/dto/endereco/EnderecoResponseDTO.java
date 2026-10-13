package br.com.henrique.estudos.ironforge_clientes.controller.dto.endereco;

import java.util.UUID;

public record EnderecoResponseDTO(
        UUID id,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep
) {}