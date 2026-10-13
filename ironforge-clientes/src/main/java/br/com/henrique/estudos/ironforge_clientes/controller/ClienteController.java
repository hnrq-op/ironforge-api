package br.com.henrique.estudos.ironforge_clientes.controller;

import br.com.henrique.estudos.ironforge_clientes.controller.dto.cliente.ClienteRequestDTO;
import br.com.henrique.estudos.ironforge_clientes.controller.dto.cliente.ClienteResponseDTO;
import br.com.henrique.estudos.ironforge_clientes.mapper.ClienteMapper;
import br.com.henrique.estudos.ironforge_clientes.model.Cliente;
import br.com.henrique.estudos.ironforge_clientes.service.ClienteService;
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
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController implements UriHeaderHelper {

    private final ClienteService service;
    private final ClienteMapper mapper;

    @PostMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> cadastrarMeuPerfil(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid ClienteRequestDTO dto) {

        Cliente cliente = mapper.toEntity(dto);
        cliente.setKeycloakId(jwt.getSubject());

        if (cliente.getEndereco() != null) {
            cliente.getEndereco().setCliente(cliente);
        }

        service.salvar(cliente);

        URI url = gerarHeaderLocation(cliente.getId());
        return ResponseEntity.created(url).build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDTO> buscarMeuPerfil(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        Cliente cliente = service.buscarPorKeycloakId(keycloakId);
        return ResponseEntity.ok(mapper.toResponseDTO(cliente));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> atualizarMeuPerfil(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid ClienteRequestDTO dto) {

        String keycloakId = jwt.getSubject();
        service.atualizarPorKeycloakId(keycloakId, dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> salvarPorAdmin(@RequestBody @Valid ClienteRequestDTO dto) {
        Cliente cliente = mapper.toEntity(dto);

        if (cliente.getEndereco() != null) {
            cliente.getEndereco().setCliente(cliente);
        }

        service.salvar(cliente);

        URI url = gerarHeaderLocation(cliente.getId());
        return ResponseEntity.created(url).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> atualizarPorId(@PathVariable UUID id, @RequestBody @Valid ClienteRequestDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarFisicamente(@PathVariable UUID id) {
        service.deletarFisicamente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable UUID id) {
        Cliente cliente = service.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponseDTO(cliente));
    }

    @GetMapping("/keycloak/{keycloakId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    public ResponseEntity<ClienteResponseDTO> buscarPorKeycloakId(@PathVariable String keycloakId) {
        Cliente cliente = service.buscarPorKeycloakId(keycloakId);
        return ResponseEntity.ok(mapper.toResponseDTO(cliente));
    }

    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    public ResponseEntity<ClienteResponseDTO> buscarPorCpf(@PathVariable String cpf) {
        Cliente cliente = service.buscarPorCpf(cpf);
        return ResponseEntity.ok(mapper.toResponseDTO(cliente));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    public ResponseEntity<ClienteResponseDTO> buscarPorEmail(@PathVariable String email) {
        Cliente cliente = service.buscarPorEmail(email);
        return ResponseEntity.ok(mapper.toResponseDTO(cliente));
    }
}