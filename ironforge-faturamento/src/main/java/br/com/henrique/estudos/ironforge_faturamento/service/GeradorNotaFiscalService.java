package br.com.henrique.estudos.ironforge_faturamento.service;

import br.com.henrique.estudos.ironforge_faturamento.mapper.NotaFiscalMapper;
import br.com.henrique.estudos.ironforge_faturamento.model.NotaFiscal;
import br.com.henrique.estudos.ironforge_faturamento.publisher.PedidoFaturadoPublisher;
import br.com.henrique.estudos.ironforge_faturamento.publisher.representation.PedidoFaturadoEventRepresentationDTO;
import br.com.henrique.estudos.ironforge_faturamento.repository.NotaFiscalRepository;
import br.com.henrique.estudos.ironforge_faturamento.subscriber.representation.PedidoPagoEventRepresentationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeradorNotaFiscalService {

    private final NotaFiscalService notaFiscalService;
    private final MinioStorageService minioStorageService;
    private final NotaFiscalRepository notaFiscalRepository;
    private final NotaFiscalMapper notaFiscalMapper;
    private final PedidoFaturadoPublisher pedidoFaturadoPublisher;

    @Transactional
    public void gerar(PedidoPagoEventRepresentationDTO pedidoEvent) {
        log.info("Iniciando faturamento para o pedido: {}", pedidoEvent.pedidoId());

        try {
            if (notaFiscalRepository.existsByPedidoId(pedidoEvent.pedidoId())) {
                log.warn("Nota Fiscal já gerada anteriormente para o pedido {}. Ignorando evento.", pedidoEvent.pedidoId());
                return;
            }

            String numeroNota = String.format("%09d", System.currentTimeMillis() % 1000000000L);
            String chaveAcesso = UUID.randomUUID().toString().replace("-", "");

            byte[] pdfBytes = notaFiscalService.gerarNota(pedidoEvent, numeroNota, chaveAcesso);

            String nomeArquivo = String.format("notafiscal_pedido_%s.pdf", pedidoEvent.pedidoId());
            String pdfUrl = minioStorageService.uploadPdf(nomeArquivo, pdfBytes);

            NotaFiscal notaFiscal = NotaFiscal.builder()
                    .pedidoId(pedidoEvent.pedidoId())
                    .numeroNota(numeroNota)
                    .chaveAcesso(chaveAcesso)
                    .pdfUrl(pdfUrl)
                    .dataEmissao(OffsetDateTime.now())
                    .build();

            NotaFiscal notaSalva = notaFiscalRepository.save(notaFiscal);

            PedidoFaturadoEventRepresentationDTO eventoSaida = notaFiscalMapper.toEventDto(notaSalva);
            pedidoFaturadoPublisher.publicar(eventoSaida);

            log.info("Nota Fiscal nº {} faturada e enviada ao Kafka para o pedido {}",
                    numeroNota, pedidoEvent.pedidoId());

        } catch (Exception e) {
            log.error("Erro ao faturar o pedido {}: {}", pedidoEvent.pedidoId(), e.getMessage(), e);
            throw new RuntimeException("Falha no processo de faturamento do pedido " + pedidoEvent.pedidoId(), e);
        }
    }
}