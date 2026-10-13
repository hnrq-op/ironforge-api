package br.com.henrique.estudos.ironforge_logistica.client;

import br.com.henrique.estudos.ironforge_logistica.model.Entrega;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ServicoLogisticaClient {

    public String solicitarEntrega(Entrega entrega){
        return UUID.randomUUID().toString();
    }
}
