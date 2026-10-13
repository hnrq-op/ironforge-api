package br.com.henrique.estudos.ironforge_pedidos.service;

import br.com.henrique.estudos.ironforge_pedidos.model.Pedido;
import br.com.henrique.estudos.ironforge_pedidos.model.enums.StatusPedido;
import br.com.henrique.estudos.ironforge_pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizacaoStatusPedidoServiceTest {

    @InjectMocks
    private AtualizacaoStatusPedidoService service;

    @Mock
    private PedidoRepository repository;

    @Test
    @DisplayName("ATUALIZAR STATUS - Deve atualizar status, urlNotaFiscal e código de rastreio quando todos os parâmetros forem informados")
    void deveAtualizarTodosOsCamposQuandoFornecidos() {
        UUID id = UUID.randomUUID();
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setStatus(StatusPedido.REALIZADO);

        when(repository.findById(id)).thenReturn(Optional.of(pedido));

        service.atualizarStatus(id, StatusPedido.ENVIADO, "http://nf.com/123.pdf", "BR123456789BR");

        assertEquals(StatusPedido.ENVIADO, pedido.getStatus());
        assertEquals("http://nf.com/123.pdf", pedido.getUrlNf());
        assertEquals("BR123456789BR", pedido.getCodigoRastreio());

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("ATUALIZAR STATUS - Deve atualizar apenas o status e manter valores preexistentes quando urlNotaFiscal e rastreio forem nulos")
    void deveAtualizarApenasStatusEPreservarDemaisCamposQuandoNulos() {
        UUID id = UUID.randomUUID();
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setStatus(StatusPedido.REALIZADO);
        pedido.setUrlNf("http://nf.com/antiga.pdf");
        pedido.setCodigoRastreio("RASTREIO_ANTIGO");

        when(repository.findById(id)).thenReturn(Optional.of(pedido));

        service.atualizarStatus(id, StatusPedido.PAGO, null, null);

        assertEquals(StatusPedido.PAGO, pedido.getStatus());
        assertEquals("http://nf.com/antiga.pdf", pedido.getUrlNf());
        assertEquals("RASTREIO_ANTIGO", pedido.getCodigoRastreio());

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("ATUALIZAR STATUS - Deve atualizar status e urlNotaFiscal sem alterar o código de rastreio quando este for nulo")
    void deveAtualizarStatusEUrlNfQuandoRastreioForNulo() {
        UUID id = UUID.randomUUID();
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setStatus(StatusPedido.PAGO);

        when(repository.findById(id)).thenReturn(Optional.of(pedido));

        service.atualizarStatus(id, StatusPedido.FATURADO, "http://nf.com/nova.pdf", null);

        assertEquals(StatusPedido.FATURADO, pedido.getStatus());
        assertEquals("http://nf.com/nova.pdf", pedido.getUrlNf());
        assertNull(pedido.getCodigoRastreio());

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("ATUALIZAR STATUS - Deve atualizar status e rastreio sem alterar urlNotaFiscal quando esta for nula")
    void deveAtualizarStatusERastreioQuandoUrlNfForNula() {
        UUID id = UUID.randomUUID();
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setStatus(StatusPedido.FATURADO);

        when(repository.findById(id)).thenReturn(Optional.of(pedido));

        service.atualizarStatus(id, StatusPedido.ENVIADO, null, "TRACK999BR");

        assertEquals(StatusPedido.ENVIADO, pedido.getStatus());
        assertNull(pedido.getUrlNf());
        assertEquals("TRACK999BR", pedido.getCodigoRastreio());

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("ATUALIZAR STATUS - Deve ignorar a atualização e não lançar exceção quando o pedido não for encontrado")
    void deveIgnorarAtualizacaoQuandoPedidoNaoEncontrado() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.atualizarStatus(id, StatusPedido.ENVIADO, "http://nf.com/123.pdf", "BR123456789BR"));

        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
    }
}