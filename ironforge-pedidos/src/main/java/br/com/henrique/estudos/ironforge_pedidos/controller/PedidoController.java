package br.com.henrique.estudos.ironforge_pedidos.controller;

import br.com.henrique.estudos.ironforge_pedidos.controller.dto.pedido.PedidoRequestDTO;
import br.com.henrique.estudos.ironforge_pedidos.controller.dto.pedido.PedidoResponseDTO;
import br.com.henrique.estudos.ironforge_pedidos.mapper.PedidoMapper;
import br.com.henrique.estudos.ironforge_pedidos.model.Pedido;
import br.com.henrique.estudos.ironforge_pedidos.service.PedidoService;
import br.com.henrique.estudos.ironforge_pedidos.validator.PedidoValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController implements UriHeaderHelper {

    private final PedidoService pedidoService;
    private final PedidoValidator validator;
    private final PedidoMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> salvar(@RequestBody @Valid PedidoRequestDTO dto) {
        Pedido pedido = mapper.toEntity(dto);
        validator.validar(pedido);
        pedidoService.salvar(pedido);

        URI url = gerarHeaderLocation(pedido.getId());
        return ResponseEntity.created(url).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<Void> atualizar(@PathVariable("id") UUID id, @RequestBody @Valid PedidoRequestDTO dto) {
        pedidoService.atualizar(id, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN', 'SERVICO')")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable("id") UUID id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponse(pedido));
    }

    @GetMapping("/{id}/detalhes")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN', 'SERVICO')")
    public ResponseEntity<PedidoResponseDTO> buscarComDadosCompletos(@PathVariable("id") UUID id) {
        Pedido pedido = pedidoService.buscarComDadosCompletos(id);
        return ResponseEntity.ok(mapper.toResponse(pedido));
    }

    @PostMapping("/{id}/solicitar-novo-pagamento")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<Void> solicitarNovoPagamento(@PathVariable("id") UUID id) {
        pedidoService.solicitarNovoPagamento(id);
        return ResponseEntity.noContent().build();
    }
}