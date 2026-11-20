package br.com.henrique.estudos.ironforge_clientes.controller;

import br.com.henrique.estudos.ironforge_clientes.controller.dto.cliente.ClienteRequestDTO;
import br.com.henrique.estudos.ironforge_clientes.controller.dto.cliente.ClienteResponseDTO;
import br.com.henrique.estudos.ironforge_clientes.controller.exception.ErroResposta;
import br.com.henrique.estudos.ironforge_clientes.mapper.ClienteMapper;
import br.com.henrique.estudos.ironforge_clientes.model.Cliente;
import br.com.henrique.estudos.ironforge_clientes.service.ClienteService;
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
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Endpoints de autoatendimento do cliente autenticado e gestão administrativa de cadastros")
public class ClienteController implements UriHeaderHelper {

    private final ClienteService service;
    private final ClienteMapper mapper;

    @PostMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(
            summary = "Cadastrar meu perfil de cliente",
            description = "Cria o cadastro de cliente vinculando automaticamente o subject (sub) do token JWT autenticado. Requer perfil CLIENTE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil criado com sucesso",
                    headers = @Header(name = "Location", description = "URI do cliente criado", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Argumento inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role CLIENTE)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "409", description = "Cliente já cadastrado para este usuário, CPF ou e-mail",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> cadastrarMeuPerfil(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid ClienteRequestDTO dto) {

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
    @Operation(
            summary = "Consultar meu perfil",
            description = "Retorna os dados cadastrais do cliente autenticado com base no subject do token JWT. Requer perfil CLIENTE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role CLIENTE)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Perfil de cliente não encontrado para o usuário autenticado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<ClienteResponseDTO> buscarMeuPerfil(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        Cliente cliente = service.buscarPorKeycloakId(keycloakId);
        return ResponseEntity.ok(mapper.toResponseDTO(cliente));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(
            summary = "Atualizar meu perfil",
            description = "Atualiza os dados cadastrais e o endereço do cliente autenticado. Requer perfil CLIENTE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Perfil atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Argumento inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role CLIENTE)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "409", description = "CPF ou e-mail já utilizado por outro cadastro",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> atualizarMeuPerfil(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid ClienteRequestDTO dto) {

        String keycloakId = jwt.getSubject();
        service.atualizarPorKeycloakId(keycloakId, dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Cadastrar cliente (Administrativo)",
            description = "Cria um novo registro de cliente diretamente pelo painel administrativo. Requer perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso",
                    headers = @Header(name = "Location", description = "URI do cliente criado", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Argumento inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "409", description = "CPF ou e-mail já cadastrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
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
    @Operation(
            summary = "Atualizar cliente por ID",
            description = "Atualiza os dados de qualquer cliente pelo seu UUID. Requer perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Argumento inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "409", description = "CPF ou e-mail já utilizado por outro cadastro",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> atualizarPorId(
            @Parameter(description = "UUID do cliente", example = "7d444840-9dc0-11d1-b245-5ffdce74fad2")
            @PathVariable UUID id,
            @RequestBody @Valid ClienteRequestDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Inativar cliente",
            description = "Realiza a desativação lógica (soft delete) do cadastro de um cliente. Requer perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente inativado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> inativar(
            @Parameter(description = "UUID do cliente", example = "7d444840-9dc0-11d1-b245-5ffdce74fad2")
            @PathVariable UUID id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Excluir cliente permanentemente",
            description = "Remove fisicamente o registro do cliente e seu endereço da base de dados. Requer perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> deletarFisicamente(
            @Parameter(description = "UUID do cliente", example = "7d444840-9dc0-11d1-b245-5ffdce74fad2")
            @PathVariable UUID id) {
        service.deletarFisicamente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    @Operation(
            summary = "Buscar cliente por ID",
            description = "Consulta os dados de um cliente pelo seu UUID. Requer perfil ADMIN ou SERVICO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN ou SERVICO)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<ClienteResponseDTO> buscarPorId(
            @Parameter(description = "UUID do cliente", example = "7d444840-9dc0-11d1-b245-5ffdce74fad2")
            @PathVariable UUID id) {
        Cliente cliente = service.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponseDTO(cliente));
    }

    @GetMapping("/keycloak/{keycloakId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    @Operation(
            summary = "Buscar cliente por Keycloak ID",
            description = "Consulta os dados de um cliente a partir do identificador subject do Keycloak. Requer perfil ADMIN ou SERVICO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN ou SERVICO)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<ClienteResponseDTO> buscarPorKeycloakId(
            @Parameter(description = "Subject ID do usuário no Keycloak", example = "a81bc81b-dead-4e5d-abff-90865d1e13b1")
            @PathVariable String keycloakId) {
        Cliente cliente = service.buscarPorKeycloakId(keycloakId);
        return ResponseEntity.ok(mapper.toResponseDTO(cliente));
    }

    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    @Operation(
            summary = "Buscar cliente por CPF",
            description = "Consulta os dados de um cliente pelo CPF cadastrado. Requer perfil ADMIN ou SERVICO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN ou SERVICO)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<ClienteResponseDTO> buscarPorCpf(
            @Parameter(description = "CPF do cliente", example = "52998224725")
            @PathVariable String cpf) {
        Cliente cliente = service.buscarPorCpf(cpf);
        return ResponseEntity.ok(mapper.toResponseDTO(cliente));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    @Operation(
            summary = "Buscar cliente por e-mail",
            description = "Consulta os dados de um cliente pelo endereço de e-mail cadastrado. Requer perfil ADMIN ou SERVICO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN ou SERVICO)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<ClienteResponseDTO> buscarPorEmail(
            @Parameter(description = "E-mail do cliente", example = "henrique.pires@email.com")
            @PathVariable String email) {
        Cliente cliente = service.buscarPorEmail(email);
        return ResponseEntity.ok(mapper.toResponseDTO(cliente));
    }
}