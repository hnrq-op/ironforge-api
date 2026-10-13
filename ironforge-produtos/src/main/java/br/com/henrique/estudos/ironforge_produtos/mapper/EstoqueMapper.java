package br.com.henrique.estudos.ironforge_produtos.mapper;

import br.com.henrique.estudos.ironforge_produtos.controller.dto.estoque.EstoqueRequestDTO;
import br.com.henrique.estudos.ironforge_produtos.controller.dto.estoque.EstoqueResponseDTO;
import br.com.henrique.estudos.ironforge_produtos.model.Estoque;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EstoqueMapper {

    @Mapping(target = "produto.id", source = "produtoId")
    Estoque toEntity(EstoqueRequestDTO dto);

    @Mapping(target = "produtoId", source = "produto.id")
    EstoqueResponseDTO toResponseDTO(Estoque estoque);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "produto", ignore = true)
    void updateEntityFromDto(EstoqueRequestDTO dto, @MappingTarget Estoque estoque);
}