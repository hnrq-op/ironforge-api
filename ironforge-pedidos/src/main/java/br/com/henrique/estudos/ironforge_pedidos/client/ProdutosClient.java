package br.com.henrique.estudos.ironforge_pedidos.client;

import br.com.henrique.estudos.ironforge_pedidos.client.representation.produto.ProdutoRepresentationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "produtos", url = "${ironforge.pedidos.clients.produtos.url}")
public interface ProdutosClient {

    @GetMapping("/produtos/{id}")
    ResponseEntity<ProdutoRepresentationDTO> obterDetalhes(@PathVariable("id") UUID id);
}