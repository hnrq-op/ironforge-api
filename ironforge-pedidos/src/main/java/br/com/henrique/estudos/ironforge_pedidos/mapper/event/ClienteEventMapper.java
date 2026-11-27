package br.com.henrique.estudos.ironforge_pedidos.mapper.event;

import br.com.henrique.estudos.ironforge_pedidos.client.representation.cliente.ClienteRepresentationDTO;
import br.com.henrique.estudos.ironforge_pedidos.publisher.representation.ClienteEventRepresentationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ClienteEventMapper {

    @Mapping(source = "id", target = "clienteId")
    @Mapping(source = "endereco", target = "enderecoEntrega")
    ClienteEventRepresentationDTO toDTO(ClienteRepresentationDTO cliente);
}