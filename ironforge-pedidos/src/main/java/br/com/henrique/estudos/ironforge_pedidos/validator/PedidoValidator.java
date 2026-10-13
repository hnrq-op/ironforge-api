package br.com.henrique.estudos.ironforge_pedidos.validator;

import br.com.henrique.estudos.ironforge_pedidos.client.ClientesClient;
import br.com.henrique.estudos.ironforge_pedidos.client.ProdutosClient;
import br.com.henrique.estudos.ironforge_pedidos.client.representation.cliente.ClienteRepresentationDTO;
import br.com.henrique.estudos.ironforge_pedidos.client.representation.produto.ProdutoRepresentationDTO;
import br.com.henrique.estudos.ironforge_pedidos.exceptions.ValidationException;
import br.com.henrique.estudos.ironforge_pedidos.model.ItemPedido;
import br.com.henrique.estudos.ironforge_pedidos.model.Pedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoValidator {

    private final ProdutosClient produtosClient;
    private final ClientesClient clientesClient;

    public void validar(Pedido pedido) {
        validarCliente(pedido.getClienteId());
        pedido.getItens().forEach(this::validarItem);
    }

    private void validarCliente(UUID clienteId) {
        var response = clientesClient.obterDetalhes(clienteId);
        ClienteRepresentationDTO cliente = response.getBody();

        if (cliente == null) {
            throw new ValidationException("clienteId", "Dados do cliente não foram retornados.");
        }

        log.info("Cliente de ID {} encontrado: {}", clienteId, cliente.nome());

        if (!cliente.ativo()) {
            throw new ValidationException("clienteId", "Cliente inativo.");
        }
    }

    private void validarItem(ItemPedido item) {
        var response = produtosClient.obterDetalhes(item.getProdutoId());
        ProdutoRepresentationDTO produto = response.getBody();

        if (produto == null) {
            throw new ValidationException("produtoId", "Dados do produto não foram retornados.");
        }

        log.info("Produto de ID {} encontrado: {}", produto.id(), produto.nome());

        if (!produto.ativo()) {
            throw new ValidationException("produtoId", "Produto inativo.");
        }
    }
}