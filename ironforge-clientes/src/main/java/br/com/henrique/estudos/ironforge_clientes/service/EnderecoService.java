package br.com.henrique.estudos.ironforge_clientes.service;

import br.com.henrique.estudos.ironforge_clientes.controller.dto.endereco.EnderecoRequestDTO;
import br.com.henrique.estudos.ironforge_clientes.mapper.EnderecoMapper;
import br.com.henrique.estudos.ironforge_clientes.model.Endereco;
import br.com.henrique.estudos.ironforge_clientes.repository.EnderecoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository repository;
    private final EnderecoMapper mapper;

    @Transactional
    public void salvar(Endereco endereco) {
        repository.save(endereco);
    }

    @Transactional
    public void atualizar(UUID id, EnderecoRequestDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("Para atualizar é necessário que o endereço esteja salvo na base (ID não pode ser nulo)");
        }

        Endereco enderecoExistente = buscarPorId(id);
        mapper.updateEntityFromDto(dto, enderecoExistente);
    }

    @Transactional
    public void deletar(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Para deletar é necessário que o endereço esteja salvo na base (ID não pode ser nulo)");
        }
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Endereço não encontrado para deletar.");
        }

        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Endereco buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public Endereco buscarPorClienteId(UUID clienteId) {
        return repository.findByClienteId(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado para o cliente de ID: " + clienteId));
    }
}