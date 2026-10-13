package br.com.henrique.estudos.ironforge_faturamento.subscriber.representation;

public record EnderecoEventRepresentationDTO(
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep
) {}