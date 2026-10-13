package br.com.henrique.estudos.ironforge_logistica.service;

import br.com.henrique.estudos.ironforge_logistica.client.ServicoLogisticaClient;
import br.com.henrique.estudos.ironforge_logistica.controller.dto.RecebimentoCallbackLogisticaDTO;
import br.com.henrique.estudos.ironforge_logistica.mapper.EntregaMapper;
import br.com.henrique.estudos.ironforge_logistica.model.Entrega;
import br.com.henrique.estudos.ironforge_logistica.model.enums.StatusEntrega;
import br.com.henrique.estudos.ironforge_logistica.publisher.PedidoEnviadoPublisher;
import br.com.henrique.estudos.ironforge_logistica.publisher.representation.PedidoEnviadoEventRepresentationDTO;
import br.com.henrique.estudos.ironforge_logistica.repository.EntregaRepository;
import br.com.henrique.estudos.ironforge_logistica.subscriber.representation.PedidoFaturadoEventRepresentationDTO;
import br.com.henrique.estudos.ironforge_logistica.validator.EntregaValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EntregaServiceTest {

    @Mock
    private EntregaRepository repository;

    @Mock
    private EntregaValidator validator;

    @Mock
    private EntregaMapper mapper;

    @Mock
    private ServicoLogisticaClient servicoLogisticaClient;

    @Mock
    private PedidoEnviadoPublisher pedidoEnviadoPublisher;

    @InjectMocks
    private EntregaService service;

    @Test
    @DisplayName("criarEntrega - Deve validar evento, solicitar chave logística e salvar entrega com status AGUARDANDO_TRANSPORTADORA")
    void deveCriarEntregaComSucesso() {
        UUID pedidoId = UUID.randomUUID();
        PedidoFaturadoEventRepresentationDTO evento = new PedidoFaturadoEventRepresentationDTO(
                pedidoId,
                UUID.randomUUID(),
                "12345",
                "chave-nfe-123",
                "http://pdf.url",
                OffsetDateTime.now()
        );

        String chaveLogisticaGerada = "chave-logistica-abc-123";
        when(servicoLogisticaClient.solicitarEntrega(any(Entrega.class))).thenReturn(chaveLogisticaGerada);

        service.criarEntrega(evento);

        verify(validator).validar(evento);
        verify(servicoLogisticaClient).solicitarEntrega(any(Entrega.class));

        ArgumentCaptor<Entrega> entregaCaptor = ArgumentCaptor.forClass(Entrega.class);
        verify(repository).save(entregaCaptor.capture());

        Entrega entregaSalva = entregaCaptor.getValue();
        assertNotNull(entregaSalva);
        assertEquals(pedidoId, entregaSalva.getPedidoId());
        assertEquals(StatusEntrega.AGUARDANDO_TRANSPORTADORA, entregaSalva.getStatus());
        assertEquals(chaveLogisticaGerada, entregaSalva.getChaveLogistica());
    }

    @Test
    @DisplayName("processarCallbackTransportadora - Deve atualizar dados, registrar dataEnvio e publicar evento no Kafka quando o novo status for ENVIADO")
    void deveProcessarCallbackEEnviarNotificacaoQuandoStatusForEnviado() {
        UUID entregaId = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();
        String chaveLogistica = "chave-valida";

        RecebimentoCallbackLogisticaDTO dto = new RecebimentoCallbackLogisticaDTO(
                entregaId,
                pedidoId,
                chaveLogistica,
                "BR987654321BR",
                StatusEntrega.ENVIADO,
                "Jadlog",
                LocalDate.now().plusDays(3)
        );

        Entrega entregaExistente = new Entrega();
        entregaExistente.setId(entregaId);
        entregaExistente.setPedidoId(pedidoId);
        entregaExistente.setChaveLogistica(chaveLogistica);
        entregaExistente.setStatus(StatusEntrega.AGUARDANDO_TRANSPORTADORA);

        PedidoEnviadoEventRepresentationDTO eventoEnviado = new PedidoEnviadoEventRepresentationDTO(
                pedidoId,
                "BR987654321BR",
                StatusEntrega.ENVIADO,
                "Jadlog",
                OffsetDateTime.now(),
                LocalDate.now().plusDays(3),
                OffsetDateTime.now()
        );

        when(repository.findByIdAndChaveLogistica(entregaId, chaveLogistica)).thenReturn(Optional.of(entregaExistente));
        when(mapper.toEventDto(entregaExistente)).thenReturn(eventoEnviado);

        service.processarCallbackTransportadora(dto);

        verify(repository).saveAndFlush(entregaExistente);
        verify(mapper).toEventDto(entregaExistente);
        verify(pedidoEnviadoPublisher).publicar(eventoEnviado);

        assertEquals(StatusEntrega.ENVIADO, entregaExistente.getStatus());
        assertEquals("BR987654321BR", entregaExistente.getCodigoRastreio());
        assertEquals("Jadlog", entregaExistente.getTransportadora());
        assertEquals(dto.dataPrevisao(), entregaExistente.getDataPrevisao());
        assertNotNull(entregaExistente.getDataEnvio());
    }

    @Test
    @DisplayName("processarCallbackTransportadora - Deve atualizar status mas não notificar no Kafka quando o status não transicionar para ENVIADO")
    void deveProcessarCallbackSemNotificarKafkaQuandoStatusNaoForEnviado() {
        UUID entregaId = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();
        String chaveLogistica = "chave-valida";

        RecebimentoCallbackLogisticaDTO dto = new RecebimentoCallbackLogisticaDTO(
                entregaId,
                pedidoId,
                chaveLogistica,
                "BR987654321BR",
                StatusEntrega.EM_TRANSITO,
                "Jadlog",
                LocalDate.now().plusDays(3)
        );

        Entrega entregaExistente = new Entrega();
        entregaExistente.setId(entregaId);
        entregaExistente.setPedidoId(pedidoId);
        entregaExistente.setChaveLogistica(chaveLogistica);
        entregaExistente.setStatus(StatusEntrega.AGUARDANDO_TRANSPORTADORA);

        when(repository.findByIdAndChaveLogistica(entregaId, chaveLogistica)).thenReturn(Optional.of(entregaExistente));

        service.processarCallbackTransportadora(dto);

        verify(repository).saveAndFlush(entregaExistente);
        verify(pedidoEnviadoPublisher, never()).publicar(any());

        assertEquals(StatusEntrega.EM_TRANSITO, entregaExistente.getStatus());
        assertEquals("BR987654321BR", entregaExistente.getCodigoRastreio());
        assertNull(entregaExistente.getDataEnvio());
    }

    @Test
    @DisplayName("processarCallbackTransportadora - Deve lançar EntityNotFoundException quando a entrega ou chave logística não forem encontradas")
    void deveLancarExcecaoQuandoEntregaOuChaveNaoExistirem() {
        UUID entregaId = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();
        String chaveInvalida = "chave-invalida";

        RecebimentoCallbackLogisticaDTO dto = new RecebimentoCallbackLogisticaDTO(
                entregaId,
                pedidoId,
                chaveInvalida,
                "BR987654321BR",
                StatusEntrega.ENVIADO,
                "Jadlog",
                LocalDate.now().plusDays(3)
        );

        when(repository.findByIdAndChaveLogistica(entregaId, chaveInvalida)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.processarCallbackTransportadora(dto)
        );

        assertTrue(exception.getMessage().contains(entregaId.toString()));
        verify(repository, never()).saveAndFlush(any());
        verify(pedidoEnviadoPublisher, never()).publicar(any());
    }

    @Test
    @DisplayName("buscarPorId - Deve retornar a entrega correspondente quando encontrar por ID")
    void deveRetornarEntregaQuandoBuscarPorIdExistente() {
        UUID entregaId = UUID.randomUUID();
        Entrega entrega = new Entrega();
        entrega.setId(entregaId);

        when(repository.findById(entregaId)).thenReturn(Optional.of(entrega));

        Entrega resultado = service.buscarPorId(entregaId);

        assertNotNull(resultado);
        assertEquals(entregaId, resultado.getId());
        verify(repository).findById(entregaId);
    }

    @Test
    @DisplayName("buscarPorId - Deve lançar EntityNotFoundException quando a entrega não for encontrada por ID")
    void deveLancarExcecaoQuandoBuscarPorIdInexistente() {
        UUID idInexistente = UUID.randomUUID();

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.buscarPorId(idInexistente)
        );

        assertEquals("Entrega não encontrada com o ID: " + idInexistente, exception.getMessage());
        verify(repository).findById(idInexistente);
    }
}