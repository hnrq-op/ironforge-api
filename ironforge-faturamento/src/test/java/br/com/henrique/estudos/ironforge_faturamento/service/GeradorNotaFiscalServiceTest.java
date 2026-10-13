package br.com.henrique.estudos.ironforge_faturamento.service;

import br.com.henrique.estudos.ironforge_faturamento.mapper.NotaFiscalMapper;
import br.com.henrique.estudos.ironforge_faturamento.model.NotaFiscal;
import br.com.henrique.estudos.ironforge_faturamento.publisher.PedidoFaturadoPublisher;
import br.com.henrique.estudos.ironforge_faturamento.publisher.representation.PedidoFaturadoEventRepresentationDTO;
import br.com.henrique.estudos.ironforge_faturamento.repository.NotaFiscalRepository;
import br.com.henrique.estudos.ironforge_faturamento.subscriber.representation.PedidoPagoEventRepresentationDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeradorNotaFiscalServiceTest {

    @Mock
    private NotaFiscalService notaFiscalService;

    @Mock
    private MinioStorageService minioStorageService;

    @Mock
    private NotaFiscalRepository notaFiscalRepository;

    @Mock
    private NotaFiscalMapper notaFiscalMapper;

    @Mock
    private PedidoFaturadoPublisher pedidoFaturadoPublisher;

    @InjectMocks
    private GeradorNotaFiscalService geradorNotaFiscalService;

    @Test
    @DisplayName("Deve faturar pedido, salvar no banco/MinIO e publicar evento com sucesso")
    void deveFaturarPedidoComSucesso() {
        UUID pedidoId = UUID.randomUUID();
        PedidoPagoEventRepresentationDTO eventoInput = new PedidoPagoEventRepresentationDTO(
                pedidoId, "PAGO", BigDecimal.valueOf(150.00), "PAY-123", LocalDateTime.now(), null, Collections.emptyList()
        );

        byte[] pdfFakeBytes = "PDF FAKE DATA".getBytes();
        String urlMinioFake = "http://localhost:9000/ironforge-faturas/notafiscal.pdf";

        NotaFiscal notaSalvaMock = NotaFiscal.builder()
                .id(UUID.randomUUID())
                .pedidoId(pedidoId)
                .numeroNota("000000001")
                .chaveAcesso("35261012345678000195550010000000011000000001")
                .pdfUrl(urlMinioFake)
                .dataEmissao(OffsetDateTime.now())
                .build();

        PedidoFaturadoEventRepresentationDTO eventoSaidaMock = new PedidoFaturadoEventRepresentationDTO(
                pedidoId, notaSalvaMock.getId(), "000000001", "35261012345678000195550010000000011000000001", urlMinioFake, OffsetDateTime.now()
        );

        when(notaFiscalRepository.existsByPedidoId(pedidoId)).thenReturn(false);
        when(notaFiscalService.gerarNota(eq(eventoInput), anyString(), anyString())).thenReturn(pdfFakeBytes);
        when(minioStorageService.uploadPdf(anyString(), eq(pdfFakeBytes))).thenReturn(urlMinioFake);
        when(notaFiscalRepository.save(any(NotaFiscal.class))).thenReturn(notaSalvaMock);
        when(notaFiscalMapper.toEventDto(notaSalvaMock)).thenReturn(eventoSaidaMock);

        geradorNotaFiscalService.gerar(eventoInput);

        verify(notaFiscalRepository).existsByPedidoId(pedidoId);
        verify(notaFiscalService).gerarNota(eq(eventoInput), anyString(), anyString());
        verify(minioStorageService).uploadPdf(anyString(), eq(pdfFakeBytes));
        verify(notaFiscalRepository).save(any(NotaFiscal.class));
        verify(pedidoFaturadoPublisher).publicar(eventoSaidaMock);
    }

    @Test
    @DisplayName("Deve ignorar o faturamento quando a Nota Fiscal já existir para o pedido")
    void deveIgnorarFaturamentoQuandoNotaJaExistir() {
        UUID pedidoId = UUID.randomUUID();
        PedidoPagoEventRepresentationDTO eventoInput = new PedidoPagoEventRepresentationDTO(
                pedidoId, "PAGO", BigDecimal.valueOf(150.00), "PAY-123", LocalDateTime.now(), null, Collections.emptyList()
        );

        when(notaFiscalRepository.existsByPedidoId(pedidoId)).thenReturn(true);

        geradorNotaFiscalService.gerar(eventoInput);

        verify(notaFiscalRepository).existsByPedidoId(pedidoId);
        verifyNoInteractions(notaFiscalService);
        verifyNoInteractions(minioStorageService);
        verify(notaFiscalRepository, never()).save(any());
        verifyNoInteractions(pedidoFaturadoPublisher);
    }

    @Test
    @DisplayName("Deve relançar exceção e interromper o fluxo quando ocorrer erro na geração")
    void deveRelancarExcecaoQuandoOcorrerErroNoFaturamento() {
        UUID pedidoId = UUID.randomUUID();
        PedidoPagoEventRepresentationDTO eventoInput = new PedidoPagoEventRepresentationDTO(
                pedidoId, "PAGO", BigDecimal.valueOf(150.00), "PAY-123", LocalDateTime.now(), null, Collections.emptyList()
        );

        when(notaFiscalRepository.existsByPedidoId(pedidoId)).thenReturn(false);
        when(notaFiscalService.gerarNota(eq(eventoInput), anyString(), anyString()))
                .thenThrow(new RuntimeException("Erro de compilação Jasper"));

        assertThatThrownBy(() -> geradorNotaFiscalService.gerar(eventoInput))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Falha no processo de faturamento do pedido " + pedidoId);

        verify(minioStorageService, never()).uploadPdf(any(), any());
        verify(notaFiscalRepository, never()).save(any());
        verifyNoInteractions(pedidoFaturadoPublisher);
    }
}