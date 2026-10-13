package br.com.henrique.estudos.ironforge_produtos.controller;

import br.com.henrique.estudos.ironforge_produtos.controller.dto.produto.ProdutoRequestDTO;
import br.com.henrique.estudos.ironforge_produtos.controller.dto.produto.ProdutoResponseDTO;
import br.com.henrique.estudos.ironforge_produtos.mapper.ProdutoMapper;
import br.com.henrique.estudos.ironforge_produtos.model.Produto;
import br.com.henrique.estudos.ironforge_produtos.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController implements UriHeaderHelper {

    private final ProdutoService service;
    private final ProdutoMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> salvar(@RequestBody @Valid ProdutoRequestDTO dto) {
        Produto produto = mapper.toEntity(dto);
        produto.setDataCriacao(OffsetDateTime.now());
        service.salvar(produto);

        URI url = gerarHeaderLocation(produto.getId());
        return ResponseEntity.created(url).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> atualizar(@PathVariable("id") UUID id, @RequestBody @Valid ProdutoRequestDTO dto) {
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
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable("id") UUID id) {
        Produto produto = service.buscarPorId(id);
        ProdutoResponseDTO dto = mapper.toResponseDTO(produto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> listarProdutosAtivos(
            @RequestParam(name = "categoria", required = false) String categoria,
            @PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<Produto> pageProdutos = service.buscarAtivosPorCategoria(categoria, pageable);
        Page<ProdutoResponseDTO> pageDto = pageProdutos.map(mapper::toResponseDTO);

        return ResponseEntity.ok(pageDto);
    }
}