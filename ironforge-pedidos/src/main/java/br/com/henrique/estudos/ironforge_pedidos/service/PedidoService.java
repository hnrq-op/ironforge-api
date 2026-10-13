package br.com.henrique.estudos.ironforge_pedidos.service;

import br.com.henrique.estudos.ironforge_pedidos.client.ClientesClient;
import br.com.henrique.estudos.ironforge_pedidos.client.ProdutosClient;
import br.com.henrique.estudos.ironforge_pedidos.client.ServicoBancarioClient;
import br.com.henrique.estudos.ironforge_pedidos.client.representation.cliente.ClienteRepresentationDTO;
import br.com.henrique.estudos.ironforge_pedidos.client.representation.produto.ProdutoRepresentationDTO;
import br.com.henrique.estudos.ironforge_pedidos.controller.dto.pedido.PedidoRequestDTO;
import br.com.henrique.estudos.ironforge_pedidos.mapper.PedidoMapper;
import br.com.henrique.estudos.ironforge_pedidos.mapper.event.PedidoPagoEventMapper;
import br.com.henrique.estudos.ironforge_pedidos.model.ItemPedido;
import br.com.henrique.estudos.ironforge_pedidos.model.Pedido;
import br.com.henrique.estudos.ironforge_pedidos.model.enums.StatusPedido;
import br.com.henrique.estudos.ironforge_pedidos.publisher.PagamentoPublisher;
import br.com.henrique.estudos.ironforge_pedidos.publisher.representation.PedidoPagoEventRepresentationDTO;
import br.com.henrique.estudos.ironforge_pedidos.repository.ItemPedidoRepository;
import br.com.henrique.estudos.ironforge_pedidos.repository.PedidoRepository;
import br.com.henrique.estudos.ironforge_pedidos.validator.PedidoValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoValidator validator;
    private final PedidoMapper pedidoMapper;
    private final PedidoPagoEventMapper pedidoPagoEventMapper;
    private final ServicoBancarioClient servicoBancarioClient;
    private final ClientesClient apiClientes;
    private final ProdutosClient apiProdutos;
    private final PagamentoPublisher pagamentoPublisher;

    @Transactional
    public void salvar(Pedido pedido) {
        criarPedido(pedido);
    }

    @Transactional
    public void criarPedido(Pedido pedido) {
        if (pedido.getDataCriacao() == null) {
            pedido.setDataCriacao(OffsetDateTime.now());
        }

        preencherDadosProdutosEValores(pedido);
        validator.validar(pedido);
        realizarPersistencia(pedido);
        enviarSolicitacaoPagamento(pedido);
    }

    private void enviarSolicitacaoPagamento(Pedido pedido) {
        String chavePagamento = servicoBancarioClient.solicitarPagamento(pedido);
        pedido.setChavePagamento(chavePagamento);
    }

    private void realizarPersistencia(Pedido pedido) {
        pedidoRepository.save(pedido);
        if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
            itemPedidoRepository.saveAll(pedido.getItens());
        }
    }

    @Transactional
    public void atualizarStatusPagamento(UUID id, String chavePagamento, boolean sucesso, String observacoes) {
        Optional<Pedido> pedidoEncontrado = pedidoRepository.findByIdAndChavePagamento(id, chavePagamento);

        if (pedidoEncontrado.isEmpty()) {
            var msg = String.format("Pedido não encontrado para o ID %s e chave pagamento %s", id, chavePagamento);
            log.error(msg);
            return;
        }

        Pedido pedido = pedidoEncontrado.get();

        if (sucesso) {
            pedido.setStatus(StatusPedido.PAGO);
            if (observacoes != null) {
                pedido.setObservacoes(observacoes);
            }

            carregarDadosCliente(pedido);
            carregarItensPedido(pedido);

            pedidoRepository.saveAndFlush(pedido);

            PedidoPagoEventRepresentationDTO evento = pedidoPagoEventMapper.toEvent(pedido, pedido.getDadosCliente());
            pagamentoPublisher.publicar(evento);
        } else {
            pedido.setStatus(StatusPedido.ERRO_PAGAMENTO);
            pedido.setObservacoes(observacoes);
            pedidoRepository.save(pedido);
        }
    }

    @Transactional
    public void solicitarNovoPagamento(UUID id) {
        Pedido pedido = buscarPorId(id);
        pedido.setStatus(StatusPedido.REALIZADO);
        pedido.setObservacoes("Nova tentativa de pagamento solicitada.");

        enviarSolicitacaoPagamento(pedido);
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void atualizar(UUID id, PedidoRequestDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("Para atualizar é necessário que o pedido esteja salvo na base (ID não pode ser nulo)");
        }

        Pedido pedidoExistente = buscarPorId(id);
        pedidoMapper.updateEntityFromDto(dto, pedidoExistente);
        preencherDadosProdutosEValores(pedidoExistente);
        validator.validar(pedidoExistente);
    }

    @Transactional(readOnly = true)
    public Pedido buscarPorId(UUID id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public Pedido buscarComDadosCompletos(UUID id) {
        Pedido pedido = buscarPorId(id);
        carregarDadosCliente(pedido);
        carregarItensPedido(pedido);
        return pedido;
    }

    private void preencherDadosProdutosEValores(Pedido pedido) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            pedido.setValorTotal(BigDecimal.ZERO);
            return;
        }

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (ItemPedido item : pedido.getItens()) {
            item.setPedido(pedido);
            carregarDadosProduto(item);

            if (item.getSubtotal() != null) {
                valorTotal = valorTotal.add(item.getSubtotal());
            }
        }

        pedido.setValorTotal(valorTotal);
    }

    private void carregarDadosCliente(Pedido pedido) {
        UUID clienteId = pedido.getClienteId();
        if (clienteId != null) {
            ResponseEntity<ClienteRepresentationDTO> response = apiClientes.obterDetalhes(clienteId);
            if (response != null && response.getBody() != null) {
                pedido.setDadosCliente(response.getBody());
            }
        }
    }

    private void carregarItensPedido(Pedido pedido) {
        if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
            pedido.getItens().forEach(this::carregarDadosProduto);
            return;
        }

        List<ItemPedido> itens = itemPedidoRepository.findByPedido(pedido);
        if (itens != null && !itens.isEmpty()) {
            pedido.getItens().clear();
            pedido.getItens().addAll(itens);
            pedido.getItens().forEach(this::carregarDadosProduto);
        }
    }

    private void carregarDadosProduto(ItemPedido item) {
        UUID produtoId = item.getProdutoId();
        if (produtoId != null) {
            ResponseEntity<ProdutoRepresentationDTO> response = apiProdutos.obterDetalhes(produtoId);
            if (response != null && response.getBody() != null) {
                ProdutoRepresentationDTO prod = response.getBody();
                item.setNome(prod.nome());
                item.setSku(prod.sku());
                if (item.getPrecoUnitario() == null) {
                    item.setPrecoUnitario(prod.preco());
                }
                if (item.getPrecoUnitario() != null && item.getQuantidade() != null) {
                    item.setSubtotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
                }
            }
        }
    }
}