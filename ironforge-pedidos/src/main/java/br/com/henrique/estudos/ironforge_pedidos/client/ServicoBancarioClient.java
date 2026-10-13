package br.com.henrique.estudos.ironforge_pedidos.client;

import br.com.henrique.estudos.ironforge_pedidos.model.Pedido;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ServicoBancarioClient {

    public String solicitarPagamento(Pedido pedido){
        return UUID.randomUUID().toString();
    }
}
