package br.com.henrique.estudos.ironforge_clientes.controller;

import br.com.henrique.estudos.ironforge_clientes.controller.dto.endereco.EnderecoRequestDTO;
import br.com.henrique.estudos.ironforge_clientes.controller.dto.endereco.EnderecoResponseDTO;
import br.com.henrique.estudos.ironforge_clientes.mapper.EnderecoMapper;
import br.com.henrique.estudos.ironforge_clientes.model.Cliente;
import br.com.henrique.estudos.ironforge_clientes.model.Endereco;
import br.com.henrique.estudos.ironforge_clientes.service.ClienteService;
import br.com.henrique.estudos.ironforge_clientes.service.EnderecoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/enderecos")
@RequiredArgsConstructor
public class EnderecoController implements UriHeaderHelper {

    private final EnderecoService enderecoService;
    private final ClienteService clienteService;
    private final EnderecoMapper mapper;

    @PostMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> cadastrarMeuEndereco(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid EnderecoRequestDTO dto) {

        Cliente cliente = clienteService.buscarPorKeycloakId(jwt.getSubject());
        Endereco endereco = mapper.toEntity(dto);
        endereco.setCliente(cliente);

        enderecoService.salvar(endereco);

        URI url = gerarHeaderLocation(endereco.getId());
        return ResponseEntity.created(url).build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<EnderecoResponseDTO> buscarMeuEndereco(@AuthenticationPrincipal Jwt jwt) {
        Cliente cliente = clienteService.buscarPorKeycloakId(jwt.getSubject());
        Endereco endereco = enderecoService.buscarPorClienteId(cliente.getId());
        return ResponseEntity.ok(mapper.toResponseDTO(endereco));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> atualizarMeuEndereco(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid EnderecoRequestDTO dto) {

        Cliente cliente = clienteService.buscarPorKeycloakId(jwt.getSubject());
        Endereco endereco = enderecoService.buscarPorClienteId(cliente.getId());

        enderecoService.atualizar(endereco.getId(), dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> salvarPorAdmin(
            @PathVariable UUID clienteId,
            @RequestBody @Valid EnderecoRequestDTO dto) {

        Cliente cliente = clienteService.buscarPorId(clienteId);
        Endereco endereco = mapper.toEntity(dto);
        endereco.setCliente(cliente);

        enderecoService.salvar(endereco);

        URI url = gerarHeaderLocation(endereco.getId());
        return ResponseEntity.created(url).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> atualizarPorId(
            @PathVariable UUID id,
            @RequestBody @Valid EnderecoRequestDTO dto) {

        enderecoService.atualizar(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        enderecoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    public ResponseEntity<EnderecoResponseDTO> buscarPorId(@PathVariable UUID id) {
        Endereco endereco = enderecoService.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponseDTO(endereco));
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    public ResponseEntity<EnderecoResponseDTO> buscarPorClienteId(@PathVariable UUID clienteId) {
        Endereco endereco = enderecoService.buscarPorClienteId(clienteId);
        return ResponseEntity.ok(mapper.toResponseDTO(endereco));
    }
}