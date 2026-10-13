package br.com.henrique.estudos.ironforge_logistica.mapper;

import br.com.henrique.estudos.ironforge_logistica.model.Entrega;
import br.com.henrique.estudos.ironforge_logistica.publisher.representation.PedidoEnviadoEventRepresentationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EntregaMapper {

    PedidoEnviadoEventRepresentationDTO toEventDto(Entrega entrega);
}
