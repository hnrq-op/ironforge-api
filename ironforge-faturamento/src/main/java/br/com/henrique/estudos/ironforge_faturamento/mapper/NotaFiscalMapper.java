package br.com.henrique.estudos.ironforge_faturamento.mapper;

import br.com.henrique.estudos.ironforge_faturamento.model.NotaFiscal;
import br.com.henrique.estudos.ironforge_faturamento.publisher.representation.PedidoFaturadoEventRepresentationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotaFiscalMapper {

    @Mapping(target = "notaFiscalId", source = "id")
    PedidoFaturadoEventRepresentationDTO toEventDto(NotaFiscal notaFiscal);
}
