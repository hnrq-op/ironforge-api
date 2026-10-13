package br.com.henrique.estudos.ironforge_pedidos.service;

import br.com.henrique.estudos.ironforge_pedidos.model.enums.StatusPedido;
import br.com.henrique.estudos.ironforge_pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtualizacaoStatusPedidoService {

    private final PedidoRepository repository;

    @Transactional
    public void atualizarStatus(UUID id, StatusPedido status, String urlNotaFiscal, String rastreio){
        repository.findById(id).ifPresent( pedido -> {
            pedido.setStatus(status);

            if (urlNotaFiscal != null){
                pedido.setUrlNf(urlNotaFiscal);
            }

            if(rastreio != null) {
                pedido.setCodigoRastreio(rastreio);
            }
        });
    }
}