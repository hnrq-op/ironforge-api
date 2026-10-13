package br.com.henrique.estudos.ironforge_clientes.mapper;

import br.com.henrique.estudos.ironforge_clientes.controller.dto.cliente.ClienteRequestDTO;
import br.com.henrique.estudos.ironforge_clientes.controller.dto.cliente.ClienteResponseDTO;
import br.com.henrique.estudos.ironforge_clientes.model.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {EnderecoMapper.class})
public interface ClienteMapper {

    Cliente toEntity(ClienteRequestDTO dto);

    ClienteResponseDTO toResponseDTO(Cliente cliente);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    void updateEntityFromDto(ClienteRequestDTO dto, @MappingTarget Cliente cliente);
}