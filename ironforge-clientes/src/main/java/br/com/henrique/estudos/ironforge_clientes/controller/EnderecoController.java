package br.com.henrique.estudos.ironforge_clientes.controller;

import br.com.henrique.estudos.ironforge_clientes.controller.dto.endereco.EnderecoRequestDTO;
import br.com.henrique.estudos.ironforge_clientes.controller.dto.endereco.EnderecoResponseDTO;
import br.com.henrique.estudos.ironforge_clientes.controller.exception.ErroResposta;
import br.com.henrique.estudos.ironforge_clientes.mapper.EnderecoMapper;
import br.com.henrique.estudos.ironforge_clientes.model.Cliente;
import br.com.henrique.estudos.ironforge_clientes.model.Endereco;
import br.com.henrique.estudos.ironforge_clientes.service.ClienteService;
import br.com.henrique.estudos.ironforge_clientes.service.EnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Endereços", description = "Endpoints para gerenciamento de endereços de entrega dos clientes")
public class EnderecoController implements UriHeaderHelper {

    private final EnderecoService enderecoService;
    private final ClienteService clienteService;
    private final EnderecoMapper mapper;

    @PostMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(
            summary = "Cadastrar meu endereço",
            description = "Cadastra o endereço vinculado ao perfil do cliente autenticado. Requer perfil CLIENTE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Endereço cadastrado com sucesso",
                    headers = @Header(name = "Location", description = "URI do endereço criado", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Argumento inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role CLIENTE)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Perfil de cliente não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "409", description = "Cliente já possui endereço cadastrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> cadastrarMeuEndereco(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
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
    @Operation(
            summary = "Consultar meu endereço",
            description = "Retorna o endereço cadastrado para o cliente autenticado. Requer perfil CLIENTE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role CLIENTE)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Cliente ou endereço não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<EnderecoResponseDTO> buscarMeuEndereco(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        Cliente cliente = clienteService.buscarPorKeycloakId(jwt.getSubject());
        Endereco endereco = enderecoService.buscarPorClienteId(cliente.getId());
        return ResponseEntity.ok(mapper.toResponseDTO(endereco));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(
            summary = "Atualizar meu endereço",
            description = "Atualiza os dados do endereço vinculado ao cliente autenticado. Requer perfil CLIENTE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Argumento inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role CLIENTE)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Cliente ou endereço não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> atualizarMeuEndereco(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid EnderecoRequestDTO dto) {

        Cliente cliente = clienteService.buscarPorKeycloakId(jwt.getSubject());
        Endereco endereco = enderecoService.buscarPorClienteId(cliente.getId());

        enderecoService.atualizar(endereco.getId(), dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Cadastrar endereço para um cliente (Administrativo)",
            description = "Cria um endereço associado ao UUID de um cliente específico. Requer perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Endereço cadastrado com sucesso",
                    headers = @Header(name = "Location", description = "URI do endereço criado", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Argumento inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "409", description = "Cliente já possui endereço cadastrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> salvarPorAdmin(
            @Parameter(description = "UUID do cliente", example = "7d444840-9dc0-11d1-b245-5ffdce74fad2")
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
    @Operation(
            summary = "Atualizar endereço por ID",
            description = "Atualiza os dados de um endereço específico pelo seu UUID. Requer perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Argumento inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> atualizarPorId(
            @Parameter(description = "UUID do endereço", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @RequestBody @Valid EnderecoRequestDTO dto) {

        enderecoService.atualizar(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Remover endereço",
            description = "Exclui um endereço pelo seu UUID. Requer perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Endereço removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "UUID do endereço", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        enderecoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    @Operation(
            summary = "Buscar endereço por ID",
            description = "Consulta os dados de um endereço pelo seu UUID. Requer perfil ADMIN ou SERVICO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN ou SERVICO)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<EnderecoResponseDTO> buscarPorId(
            @Parameter(description = "UUID do endereço", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        Endereco endereco = enderecoService.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponseDTO(endereco));
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    @Operation(
            summary = "Buscar endereço por ID do cliente",
            description = "Consulta o endereço cadastrado a partir do UUID do cliente. Requer perfil ADMIN ou SERVICO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Endereço do cliente encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN ou SERVICO)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Cliente ou endereço não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<EnderecoResponseDTO> buscarPorClienteId(
            @Parameter(description = "UUID do cliente", example = "7d444840-9dc0-11d1-b245-5ffdce74fad2")
            @PathVariable UUID clienteId) {
        Endereco endereco = enderecoService.buscarPorClienteId(clienteId);
        return ResponseEntity.ok(mapper.toResponseDTO(endereco));
    }
}