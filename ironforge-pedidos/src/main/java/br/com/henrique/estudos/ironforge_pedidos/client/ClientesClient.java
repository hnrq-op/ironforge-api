package br.com.henrique.estudos.ironforge_pedidos.client;

import br.com.henrique.estudos.ironforge_pedidos.client.representation.cliente.ClienteRepresentationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "clientes", url = "${ironforge.pedidos.clients.clientes.url}")
public interface ClientesClient {

    @GetMapping("/clientes/{id}")
    ResponseEntity<ClienteRepresentationDTO> obterDetalhes(@PathVariable("id") UUID id);
}
