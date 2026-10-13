package br.com.henrique.estudos.ironforge_pedidos.mapper.event;

import br.com.henrique.estudos.ironforge_pedidos.client.representation.cliente.ClienteRepresentationDTO;
import br.com.henrique.estudos.ironforge_pedidos.client.representation.cliente.EnderecoRepresentationDTO;
import br.com.henrique.estudos.ironforge_pedidos.publisher.representation.ClienteEventRepresentationDTO;
import br.com.henrique.estudos.ironforge_pedidos.publisher.representation.EnderecoEventRepresentationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteEventMapper {

    @Mapping(source = "id", target = "clienteId")
    @Mapping(source = "endereco", target = "enderecoEntrega")
    ClienteEventRepresentationDTO toDTO(ClienteRepresentationDTO cliente);
}