package br.com.henrique.estudos.ironforge_produtos.controller;

import br.com.henrique.estudos.ironforge_produtos.controller.dto.estoque.EstoqueRequestDTO;
import br.com.henrique.estudos.ironforge_produtos.controller.dto.estoque.EstoqueResponseDTO;
import br.com.henrique.estudos.ironforge_produtos.mapper.EstoqueMapper;
import br.com.henrique.estudos.ironforge_produtos.model.Estoque;
import br.com.henrique.estudos.ironforge_produtos.service.EstoqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/estoques")
@RequiredArgsConstructor
public class EstoqueController implements UriHeaderHelper {

    private final EstoqueService service;
    private final EstoqueMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> salvar(@RequestBody @Valid EstoqueRequestDTO dto) {
        Estoque estoque = mapper.toEntity(dto);
        service.salvar(estoque);

        URI url = gerarHeaderLocation(estoque.getId());
        return ResponseEntity.created(url).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> atualizar(@PathVariable("id") UUID id, @RequestBody @Valid EstoqueRequestDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable("id") UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    public ResponseEntity<EstoqueResponseDTO> buscarPorId(@PathVariable("id") UUID id) {
        Estoque estoque = service.buscarPorId(id);
        EstoqueResponseDTO dto = mapper.toResponseDTO(estoque);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/produto/{produtoId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    public ResponseEntity<EstoqueResponseDTO> buscarPorProdutoId(@PathVariable("produtoId") UUID produtoId) {
        Estoque estoque = service.buscarPorProdutoId(produtoId);
        EstoqueResponseDTO dto = mapper.toResponseDTO(estoque);
        return ResponseEntity.ok(dto);
    }
}