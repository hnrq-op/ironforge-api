package br.com.henrique.estudos.ironforge_logistica.controller;

import br.com.henrique.estudos.ironforge_logistica.controller.dto.RecebimentoCallbackLogisticaDTO;
import br.com.henrique.estudos.ironforge_logistica.service.EntregaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logistica/callback-logistica")
@RequiredArgsConstructor
public class RecebimentoCallbackLogisticaController {

    private final EntregaService service;

    @PostMapping
    public ResponseEntity<Void> atualizarStatusEntrega(@RequestBody @Valid RecebimentoCallbackLogisticaDTO dto, @RequestHeader(name = "apiKey") String apiKey) {
        service.processarCallbackTransportadora(dto);
        return ResponseEntity.ok().build();
    }
}