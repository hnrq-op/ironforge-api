package br.com.henrique.estudos.ironforge_pedidos.controller;

import br.com.henrique.estudos.ironforge_pedidos.controller.dto.pagamento.RecebimentoCallbackPagamentoDTO;
import br.com.henrique.estudos.ironforge_pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos/callback-pagamentos")
@RequiredArgsConstructor
public class RecebimentoCallbackPagamentoController {

    private final PedidoService service;

    @PostMapping
    public ResponseEntity<Void> atualizarStatusPagamento(@RequestBody RecebimentoCallbackPagamentoDTO body, @RequestHeader(name = "apiKey") String apiKey) {

        service.atualizarStatusPagamento(
                body.id(),
                body.chavePagamento(),
                body.status(),
                body.observacoes()
        );

        return ResponseEntity.ok().build();
    }
}