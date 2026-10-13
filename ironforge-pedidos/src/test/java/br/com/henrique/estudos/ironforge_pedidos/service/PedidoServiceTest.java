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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @InjectMocks
    private PedidoService service;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @Mock
    private PedidoValidator validator;

    @Mock
    private PedidoMapper pedidoMapper;

    @Mock
    private PedidoPagoEventMapper pedidoPagoEventMapper;

    @Mock
    private ServicoBancarioClient servicoBancarioClient;

    @Mock
    private ClientesClient apiClientes;

    @Mock
    private ProdutosClient apiProdutos;

    @Mock
    private PagamentoPublisher pagamentoPublisher;


    @MockitoBean
    private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;

    @MockitoBean
    private org.springframework.security.oauth2.client.registration.ClientRegistrationRepository clientRegistrationRepository;

    @Test
    @DisplayName("CRIAR PEDIDO - Deve criar pedido com sucesso, preencher dataCriacao e solicitar pagamento")
    void deveCriarPedidoComSucessoEPreencherDataCriacao() {
        UUID produtoId = UUID.randomUUID();
        ItemPedido item = new ItemPedido();
        item.setProdutoId(produtoId);
        item.setQuantidade(2);

        Pedido pedido = new Pedido();
        pedido.getItens().add(item);

        ProdutoRepresentationDTO prodDto = mock(ProdutoRepresentationDTO.class);
        when(prodDto.nome()).thenReturn("Produto Teste");
        when(prodDto.preco()).thenReturn(BigDecimal.valueOf(50.00));
        when(apiProdutos.obterDetalhes(produtoId)).thenReturn(ResponseEntity.ok(prodDto));
        when(servicoBancarioClient.solicitarPagamento(pedido)).thenReturn("CHAVE-PAY-123");

        service.criarPedido(pedido);

        assertNotNull(pedido.getDataCriacao());
        assertEquals("CHAVE-PAY-123", pedido.getChavePagamento());
        assertEquals(BigDecimal.valueOf(100.00), pedido.getValorTotal());
        assertEquals(pedido, item.getPedido());
        assertEquals("Produto Teste", item.getNome());
        assertEquals(BigDecimal.valueOf(50.00), item.getPrecoUnitario());
        assertEquals(BigDecimal.valueOf(100.00), item.getSubtotal());

        verify(validator, times(1)).validar(pedido);
        verify(pedidoRepository, times(1)).save(pedido);
        verify(itemPedidoRepository, times(1)).saveAll(pedido.getItens());
        verify(servicoBancarioClient, times(1)).solicitarPagamento(pedido);
    }

    @Test
    @DisplayName("CRIAR PEDIDO - Deve manter dataCriacao existente e tratar pedido sem itens")
    void deveCriarPedidoComDataCriacaoExistenteESemItens() {
        OffsetDateTime dataExistente = OffsetDateTime.now().minusDays(1);
        Pedido pedido = new Pedido();
        pedido.setDataCriacao(dataExistente);

        when(servicoBancarioClient.solicitarPagamento(pedido)).thenReturn("CHAVE-PAY-456");

        service.criarPedido(pedido);

        assertEquals(dataExistente, pedido.getDataCriacao());
        assertEquals(BigDecimal.ZERO, pedido.getValorTotal());
        assertEquals("CHAVE-PAY-456", pedido.getChavePagamento());

        verify(validator).validar(pedido);
        verify(pedidoRepository).save(pedido);
        verify(itemPedidoRepository, never()).saveAll(any());
        verify(servicoBancarioClient).solicitarPagamento(pedido);
    }

    @Test
    @DisplayName("CRIAR PEDIDO - Deve lançar exceção e interromper fluxo quando validador falhar")
    void deveLancarExcecaoEInterromperCriacaoQuandoValidadorFalhar() {
        Pedido pedido = new Pedido();
        doThrow(new IllegalArgumentException("Pedido sem cliente")).when(validator).validar(pedido);

        assertThrows(IllegalArgumentException.class, () -> service.criarPedido(pedido));

        verify(pedidoRepository, never()).save(any());
        verify(servicoBancarioClient, never()).solicitarPagamento(any());
    }

    @Test
    @DisplayName("SALVAR - Deve delegar para criarPedido com sucesso")
    void deveSalvarDelegandoParaCriarPedido() {
        Pedido pedido = new Pedido();
        when(servicoBancarioClient.solicitarPagamento(pedido)).thenReturn("CHAVE-PAY-789");

        service.salvar(pedido);

        assertNotNull(pedido.getDataCriacao());
        verify(validator).validar(pedido);
        verify(pedidoRepository).save(pedido);
        verify(servicoBancarioClient).solicitarPagamento(pedido);
    }

    @Test
    @DisplayName("PREENCHER PRODUTOS - Deve manter preço unitário preexistente no item caso já preenchido")
    void deveManterPrecoUnitarioPreexistenteNoItem() {
        UUID produtoId = UUID.randomUUID();
        ItemPedido item = new ItemPedido();
        item.setProdutoId(produtoId);
        item.setQuantidade(2);
        item.setPrecoUnitario(BigDecimal.valueOf(100.00));

        Pedido pedido = new Pedido();
        pedido.getItens().add(item);

        ProdutoRepresentationDTO prodDto = mock(ProdutoRepresentationDTO.class);
        when(prodDto.nome()).thenReturn("Produto Promocional");
        when(apiProdutos.obterDetalhes(produtoId)).thenReturn(ResponseEntity.ok(prodDto));
        when(servicoBancarioClient.solicitarPagamento(pedido)).thenReturn("CHAVE-123");

        service.criarPedido(pedido);

        assertEquals(BigDecimal.valueOf(100.00), item.getPrecoUnitario());
        assertEquals(BigDecimal.valueOf(200.00), item.getSubtotal());
        assertEquals(BigDecimal.valueOf(200.00), pedido.getValorTotal());
    }

    @Test
    @DisplayName("ATUALIZAR STATUS PAGAMENTO - Deve atualizar para PAGO, carregar dados e publicar evento quando sucesso for true")
    void deveAtualizarStatusPagamentoParaPagoEPublicarEvento() {
        UUID id = UUID.randomUUID();
        String chave = "CHAVE-123";
        UUID clienteId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        ItemPedido item = new ItemPedido();
        item.setProdutoId(produtoId);

        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setClienteId(clienteId);
        pedido.getItens().add(item);

        ClienteRepresentationDTO clienteDto = mock(ClienteRepresentationDTO.class);
        ProdutoRepresentationDTO produtoDto = mock(ProdutoRepresentationDTO.class);
        PedidoPagoEventRepresentationDTO eventoDto = mock(PedidoPagoEventRepresentationDTO.class);

        when(pedidoRepository.findByIdAndChavePagamento(id, chave)).thenReturn(Optional.of(pedido));
        when(apiClientes.obterDetalhes(clienteId)).thenReturn(ResponseEntity.ok(clienteDto));
        when(apiProdutos.obterDetalhes(produtoId)).thenReturn(ResponseEntity.ok(produtoDto));
        when(pedidoPagoEventMapper.toEvent(pedido, clienteDto)).thenReturn(eventoDto);

        service.atualizarStatusPagamento(id, chave, true, "Pagamento confirmado via Pix");

        assertEquals(StatusPedido.PAGO, pedido.getStatus());
        assertEquals("Pagamento confirmado via Pix", pedido.getObservacoes());
        assertEquals(clienteDto, pedido.getDadosCliente());

        verify(pedidoRepository).saveAndFlush(pedido);
        verify(pagamentoPublisher).publicar(eventoDto);
    }

    @Test
    @DisplayName("ATUALIZAR STATUS PAGAMENTO - Deve atualizar para PAGO sem alterar observacoes quando forem nulas")
    void deveAtualizarStatusPagamentoParaPagoComObservacoesNulas() {
        UUID id = UUID.randomUUID();
        String chave = "CHAVE-123";
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setObservacoes("Observação Antiga");

        when(pedidoRepository.findByIdAndChavePagamento(id, chave)).thenReturn(Optional.of(pedido));
        when(pedidoPagoEventMapper.toEvent(eq(pedido), any())).thenReturn(mock(PedidoPagoEventRepresentationDTO.class));

        service.atualizarStatusPagamento(id, chave, true, null);

        assertEquals(StatusPedido.PAGO, pedido.getStatus());
        assertEquals("Observação Antiga", pedido.getObservacoes());
        verify(pedidoRepository).saveAndFlush(pedido);
    }

    @Test
    @DisplayName("ATUALIZAR STATUS PAGAMENTO - Deve atualizar para ERRO_PAGAMENTO quando sucesso for false")
    void deveAtualizarStatusPagamentoParaErro() {
        UUID id = UUID.randomUUID();
        String chave = "CHAVE-123";
        Pedido pedido = new Pedido();
        pedido.setId(id);

        when(pedidoRepository.findByIdAndChavePagamento(id, chave)).thenReturn(Optional.of(pedido));

        service.atualizarStatusPagamento(id, chave, false, "Cartão recusado");

        assertEquals(StatusPedido.ERRO_PAGAMENTO, pedido.getStatus());
        assertEquals("Cartão recusado", pedido.getObservacoes());

        verify(pedidoRepository).save(pedido);
        verify(pedidoRepository, never()).saveAndFlush(any());
        verifyNoInteractions(pagamentoPublisher, apiClientes);
    }

    @Test
    @DisplayName("ATUALIZAR STATUS PAGAMENTO - Deve apenas logar e ignorar quando pedido não for encontrado")
    void deveIgnorarAtualizacaoStatusQuandoPedidoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        String chave = "CHAVE-INEXISTENTE";

        when(pedidoRepository.findByIdAndChavePagamento(id, chave)).thenReturn(Optional.empty());

        service.atualizarStatusPagamento(id, chave, true, "Obs");

        verify(pedidoRepository, never()).save(any());
        verify(pedidoRepository, never()).saveAndFlush(any());
        verifyNoInteractions(pagamentoPublisher, apiClientes, apiProdutos);
    }

    @Test
    @DisplayName("SOLICITAR NOVO PAGAMENTO - Deve alterar status para REALIZADO e solicitar nova chave com sucesso")
    void deveSolicitarNovoPagamentoComSucesso() {
        UUID id = UUID.randomUUID();
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setStatus(StatusPedido.ERRO_PAGAMENTO);

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        when(servicoBancarioClient.solicitarPagamento(pedido)).thenReturn("NOVA-CHAVE-999");

        service.solicitarNovoPagamento(id);

        assertEquals(StatusPedido.REALIZADO, pedido.getStatus());
        assertEquals("Nova tentativa de pagamento solicitada.", pedido.getObservacoes());
        assertEquals("NOVA-CHAVE-999", pedido.getChavePagamento());

        verify(pedidoRepository).findById(id);
        verify(servicoBancarioClient).solicitarPagamento(pedido);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    @DisplayName("SOLICITAR NOVO PAGAMENTO - Deve lançar EntityNotFoundException quando o pedido não existir")
    void deveLancarExcecaoAoSolicitarNovoPagamentoParaPedidoInexistente() {
        UUID id = UUID.randomUUID();
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.solicitarNovoPagamento(id));

        verify(pedidoRepository).findById(id);
        verifyNoInteractions(servicoBancarioClient);
    }

    @Test
    @DisplayName("ATUALIZAR - Deve atualizar o pedido com sucesso quando o ID existir")
    void deveAtualizarPedidoPorIdComSucesso() {
        UUID id = UUID.randomUUID();
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(id);
        PedidoRequestDTO dto = mock(PedidoRequestDTO.class);

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedidoExistente));

        service.atualizar(id, dto);

        verify(pedidoRepository).findById(id);
        verify(pedidoMapper).updateEntityFromDto(dto, pedidoExistente);
        verify(validator).validar(pedidoExistente);
    }

    @Test
    @DisplayName("ATUALIZAR - Deve lançar IllegalArgumentException quando o ID for nulo")
    void deveLancarExcecaoAoAtualizarComIdNulo() {
        PedidoRequestDTO dto = mock(PedidoRequestDTO.class);

        assertThrows(IllegalArgumentException.class, () -> service.atualizar(null, dto));

        verifyNoInteractions(pedidoRepository, pedidoMapper, validator);
    }

    @Test
    @DisplayName("ATUALIZAR - Deve lançar EntityNotFoundException quando o pedido não existir")
    void deveLancarExcecaoAoAtualizarPedidoInexistente() {
        UUID id = UUID.randomUUID();
        PedidoRequestDTO dto = mock(PedidoRequestDTO.class);

        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.atualizar(id, dto));

        verify(pedidoRepository).findById(id);
        verifyNoInteractions(pedidoMapper, validator);
    }

    @Test
    @DisplayName("BUSCAR POR ID - Deve retornar Pedido quando encontrado")
    void deveBuscarPorIdComSucesso() {
        UUID id = UUID.randomUUID();
        Pedido pedido = new Pedido();
        pedido.setId(id);

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));

        Pedido resultado = service.buscarPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        verify(pedidoRepository).findById(id);
    }

    @Test
    @DisplayName("BUSCAR POR ID - Deve lançar EntityNotFoundException quando não encontrado")
    void deveLancarExcecaoAoBuscarPorIdInexistente() {
        UUID id = UUID.randomUUID();
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.buscarPorId(id));
        verify(pedidoRepository).findById(id);
    }

    @Test
    @DisplayName("BUSCAR COM DADOS COMPLETOS - Deve buscar pedido e carregar dados do cliente e itens do repository caso vazios")
    void deveBuscarComDadosCompletosECarregarItensDoRepository() {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setClienteId(clienteId);

        ItemPedido itemBanco = new ItemPedido();
        itemBanco.setProdutoId(produtoId);

        ClienteRepresentationDTO clienteDto = mock(ClienteRepresentationDTO.class);
        ProdutoRepresentationDTO produtoDto = mock(ProdutoRepresentationDTO.class);

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        when(apiClientes.obterDetalhes(clienteId)).thenReturn(ResponseEntity.ok(clienteDto));
        when(itemPedidoRepository.findByPedido(pedido)).thenReturn(List.of(itemBanco));
        when(apiProdutos.obterDetalhes(produtoId)).thenReturn(ResponseEntity.ok(produtoDto));

        Pedido resultado = service.buscarComDadosCompletos(id);

        assertNotNull(resultado);
        assertEquals(clienteDto, resultado.getDadosCliente());
        assertEquals(1, resultado.getItens().size());
        assertEquals(itemBanco, resultado.getItens().get(0));

        verify(apiClientes).obterDetalhes(clienteId);
        verify(itemPedidoRepository).findByPedido(pedido);
        verify(apiProdutos).obterDetalhes(produtoId);
    }

    @Test
    @DisplayName("BUSCAR COM DADOS COMPLETOS - Deve usar itens já existentes sem consultar o repository")
    void deveCarregarDadosDosItensSemConsultarRepositoryQuandoJaEstiveremCarregados() {
        UUID id = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        ItemPedido item = new ItemPedido();
        item.setProdutoId(produtoId);

        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.getItens().add(item);

        ProdutoRepresentationDTO prodDto = mock(ProdutoRepresentationDTO.class);
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        when(apiProdutos.obterDetalhes(produtoId)).thenReturn(ResponseEntity.ok(prodDto));

        Pedido resultado = service.buscarComDadosCompletos(id);

        assertNotNull(resultado);
        verify(itemPedidoRepository, never()).findByPedido(any());
        verify(apiProdutos).obterDetalhes(produtoId);
    }

    @Test
    @DisplayName("CARREGAR CLIENTE - Deve ignorar preenchimento quando clienteId for nulo ou resposta da API for vazia")
    void deveIgnorarPreenchimentoClienteQuandoResponseForVazioOuClienteIdNulo() {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setClienteId(clienteId);

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        when(apiClientes.obterDetalhes(clienteId)).thenReturn(ResponseEntity.ok(null));

        Pedido resultado = service.buscarComDadosCompletos(id);

        assertNull(resultado.getDadosCliente());
        verify(apiClientes).obterDetalhes(clienteId);
    }

    @Test
    @DisplayName("CARREGAR PRODUTO - Deve tratar graciosamente quando a API de produtos retornar resposta nula ou produtoId for nulo")
    void deveTratarGraciosamenteQuandoApiProdutosRetornarNuloOuProdutoIdNulo() {
        UUID id = UUID.randomUUID();
        ItemPedido itemSemId = new ItemPedido(); // produtoId nulo
        ItemPedido itemComId = new ItemPedido();
        UUID prodId = UUID.randomUUID();
        itemComId.setProdutoId(prodId);

        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.getItens().add(itemSemId);
        pedido.getItens().add(itemComId);

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        when(apiProdutos.obterDetalhes(prodId)).thenReturn(null);

        Pedido resultado = service.buscarComDadosCompletos(id);

        assertNotNull(resultado);
        assertNull(itemComId.getNome());
        verify(apiProdutos, times(1)).obterDetalhes(prodId);
    }

    @Test
    @DisplayName("CARREGAR ITENS - Deve tratar quando o repository retornar lista nula em carregarItensPedido")
    void deveTratarListaNulaDoRepositoryEmCarregarItens() {
        UUID id = UUID.randomUUID();
        Pedido pedido = new Pedido();
        pedido.setId(id);

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        when(itemPedidoRepository.findByPedido(pedido)).thenReturn(null);

        Pedido resultado = service.buscarComDadosCompletos(id);

        assertNotNull(resultado);
        assertTrue(resultado.getItens().isEmpty());
        verify(itemPedidoRepository).findByPedido(pedido);
        verifyNoInteractions(apiProdutos);
    }
}