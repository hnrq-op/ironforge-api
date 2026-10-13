package br.com.henrique.estudos.ironforge_clientes.service;

import br.com.henrique.estudos.ironforge_clientes.controller.dto.cliente.ClienteRequestDTO;
import br.com.henrique.estudos.ironforge_clientes.mapper.ClienteMapper;
import br.com.henrique.estudos.ironforge_clientes.model.Cliente;
import br.com.henrique.estudos.ironforge_clientes.repository.ClienteRepository;
import br.com.henrique.estudos.ironforge_clientes.validator.ClienteValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;
    private final ClienteValidator validator;
    private final ClienteMapper mapper;

    @Transactional
    public void salvar(Cliente cliente) {
        if (cliente.getDataCriacao() == null) {
            cliente.setDataCriacao(OffsetDateTime.now());
        }
        validator.validar(cliente);
        repository.save(cliente);
    }

    @Transactional
    public void atualizar(UUID id, ClienteRequestDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("Para atualizar é necessário que o cliente esteja salvo na base (ID não pode ser nulo)");
        }

        Cliente clienteExistente = buscarPorId(id);
        mapper.updateEntityFromDto(dto, clienteExistente);
        validator.validar(clienteExistente);
    }

    @Transactional
    public void atualizarPorKeycloakId(String keycloakId, ClienteRequestDTO dto) {
        if (keycloakId == null || keycloakId.isBlank()) {
            throw new IllegalArgumentException("Keycloak ID inválido para atualização.");
        }

        Cliente clienteExistente = buscarPorKeycloakId(keycloakId);
        mapper.updateEntityFromDto(dto, clienteExistente);
        validator.validar(clienteExistente);
    }

    @Transactional
    public void inativar(UUID id) {
        Cliente cliente = buscarPorId(id);
        cliente.setAtivo(false);
    }

    @Transactional
    public void deletarFisicamente(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Para deletar é necessário que o cliente esteja salvo na base (ID não pode ser nulo)");
        }
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Cliente não encontrado para deletar.");
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorKeycloakId(String keycloakId) {
        return repository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o Keycloak ID: " + keycloakId));
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorCpf(String cpf) {
        return repository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o CPF: " + cpf));
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o email: " + email));
    }
}