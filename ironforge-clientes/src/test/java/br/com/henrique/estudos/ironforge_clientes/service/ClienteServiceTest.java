package br.com.henrique.estudos.ironforge_clientes.service;

import br.com.henrique.estudos.ironforge_clientes.controller.dto.cliente.ClienteRequestDTO;
import br.com.henrique.estudos.ironforge_clientes.exceptions.RegistroDuplicadoException;
import br.com.henrique.estudos.ironforge_clientes.mapper.ClienteMapper;
import br.com.henrique.estudos.ironforge_clientes.model.Cliente;
import br.com.henrique.estudos.ironforge_clientes.repository.ClienteRepository;
import br.com.henrique.estudos.ironforge_clientes.validator.ClienteValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @InjectMocks
    private ClienteService service;

    @Mock
    private ClienteRepository repository;

    @Mock
    private ClienteValidator validator;

    @Mock
    private ClienteMapper mapper;

    @Test
    @DisplayName("SALVAR - Deve salvar o cliente com sucesso e atribuir dataCriacao se nula")
    void deveSalvarClienteComSucessoEPreencherDataCriacao() {
        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNome("Cliente Teste");
        cliente.setCpf("12345678900");
        cliente.setEmail("cliente@teste.com");
        cliente.setKeycloakId("keycloak-123");

        service.salvar(cliente);

        assertNotNull(cliente.getDataCriacao());
        verify(validator, times(1)).validar(cliente);
        verify(repository, times(1)).save(cliente);
    }

    @Test
    @DisplayName("SALVAR - Deve manter a dataCriacao existente caso não seja nula")
    void deveSalvarClienteMantendoDataCriacaoExistente() {
        OffsetDateTime dataExistente = OffsetDateTime.now().minusDays(1);
        Cliente cliente = new Cliente();
        cliente.setDataCriacao(dataExistente);

        service.salvar(cliente);

        assertEquals(dataExistente, cliente.getDataCriacao());
        verify(validator).validar(cliente);
        verify(repository).save(cliente);
    }

    @Test
    @DisplayName("SALVAR - Deve lançar exceção quando o validator encontrar duplicidade")
    void deveLancarExcecaoAoSalvarClienteDuplicado() {
        Cliente cliente = new Cliente();
        doThrow(new RegistroDuplicadoException("CPF já cadastrado")).when(validator).validar(cliente);

        assertThrows(RegistroDuplicadoException.class, () -> service.salvar(cliente));

        verify(repository, never()).save(cliente);
    }

    @Test
    @DisplayName("ATUALIZAR POR ID - Deve atualizar o cliente com sucesso quando o ID existir")
    void deveAtualizarClientePorIdComSucesso() {
        UUID id = UUID.randomUUID();
        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(id);
        ClienteRequestDTO dto = mock(ClienteRequestDTO.class);

        when(repository.findById(id)).thenReturn(Optional.of(clienteExistente));

        service.atualizar(id, dto);

        verify(repository).findById(id);
        verify(mapper).updateEntityFromDto(dto, clienteExistente);
        verify(validator).validar(clienteExistente);
    }

    @Test
    @DisplayName("ATUALIZAR POR ID - Deve lançar IllegalArgumentException quando o ID for nulo")
    void deveLancarExcecaoAoAtualizarComIdNulo() {
        ClienteRequestDTO dto = mock(ClienteRequestDTO.class);

        assertThrows(IllegalArgumentException.class, () -> service.atualizar(null, dto));

        verifyNoInteractions(repository, mapper, validator);
    }

    @Test
    @DisplayName("ATUALIZAR POR ID - Deve lançar EntityNotFoundException quando cliente não existir")
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        UUID id = UUID.randomUUID();
        ClienteRequestDTO dto = mock(ClienteRequestDTO.class);

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.atualizar(id, dto));

        verify(repository).findById(id);
        verifyNoInteractions(mapper, validator);
    }

    @Test
    @DisplayName("ATUALIZAR POR KEYCLOAK ID - Deve atualizar com sucesso quando o Keycloak ID for válido")
    void deveAtualizarPorKeycloakIdComSucesso() {
        String keycloakId = "keycloak-123";
        Cliente clienteExistente = new Cliente();
        clienteExistente.setKeycloakId(keycloakId);
        ClienteRequestDTO dto = mock(ClienteRequestDTO.class);

        when(repository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(clienteExistente));

        service.atualizarPorKeycloakId(keycloakId, dto);

        verify(repository).findByKeycloakId(keycloakId);
        verify(mapper).updateEntityFromDto(dto, clienteExistente);
        verify(validator).validar(clienteExistente);
    }

    @Test
    @DisplayName("ATUALIZAR POR KEYCLOAK ID - Deve lançar IllegalArgumentException quando Keycloak ID for nulo ou em branco")
    void deveLancarExcecaoAoAtualizarComKeycloakIdInvalido() {
        ClienteRequestDTO dto = mock(ClienteRequestDTO.class);

        assertThrows(IllegalArgumentException.class, () -> service.atualizarPorKeycloakId(null, dto));
        assertThrows(IllegalArgumentException.class, () -> service.atualizarPorKeycloakId("   ", dto));

        verifyNoInteractions(repository, mapper, validator);
    }

    @Test
    @DisplayName("INATIVAR - Deve inativar o cliente com sucesso alterando o atributo ativo para false")
    void deveInativarClienteComSucesso() {
        UUID id = UUID.randomUUID();
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setAtivo(true);

        when(repository.findById(id)).thenReturn(Optional.of(cliente));

        service.inativar(id);

        assertFalse(cliente.isAtivo());
        verify(repository).findById(id);
    }

    @Test
    @DisplayName("DELETAR FISICAMENTE - Deve deletar o cliente com sucesso quando o ID existir")
    void deveDeletarFisicamenteComSucesso() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.deletarFisicamente(id);

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("DELETAR FISICAMENTE - Deve lançar IllegalArgumentException quando o ID for nulo")
    void deveLancarExcecaoAoDeletarFisicamenteComIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.deletarFisicamente(null));

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("DELETAR FISICAMENTE - Deve lançar EntityNotFoundException quando o ID não existir")
    void deveLancarExcecaoAoDeletarFisicamenteIdInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.deletarFisicamente(id));

        verify(repository).existsById(id);
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("BUSCAR POR ID - Deve retornar Cliente quando encontrado")
    void deveBuscarPorIdComSucesso() {
        UUID id = UUID.randomUUID();
        Cliente cliente = new Cliente();
        cliente.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(cliente));

        Cliente resultado = service.buscarPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        verify(repository).findById(id);
    }

    @Test
    @DisplayName("BUSCAR POR ID - Deve lançar EntityNotFoundException quando não encontrado")
    void deveLancarExcecaoAoBuscarPorIdInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.buscarPorId(id));
    }

    @Test
    @DisplayName("BUSCAR POR KEYCLOAK ID - Deve retornar Cliente quando encontrado")
    void deveBuscarPorKeycloakIdComSucesso() {
        String keycloakId = "keycloak-123";
        Cliente cliente = new Cliente();
        cliente.setKeycloakId(keycloakId);

        when(repository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(cliente));

        Cliente resultado = service.buscarPorKeycloakId(keycloakId);

        assertNotNull(resultado);
        assertEquals(keycloakId, resultado.getKeycloakId());
        verify(repository).findByKeycloakId(keycloakId);
    }

    @Test
    @DisplayName("BUSCAR POR CPF - Deve retornar Cliente quando encontrado")
    void deveBuscarPorCpfComSucesso() {
        String cpf = "12345678900";
        Cliente cliente = new Cliente();
        cliente.setCpf(cpf);

        when(repository.findByCpf(cpf)).thenReturn(Optional.of(cliente));

        Cliente resultado = service.buscarPorCpf(cpf);

        assertNotNull(resultado);
        assertEquals(cpf, resultado.getCpf());
        verify(repository).findByCpf(cpf);
    }

    @Test
    @DisplayName("BUSCAR POR EMAIL - Deve retornar Cliente quando encontrado")
    void deveBuscarPorEmailComSucesso() {
        String email = "cliente@teste.com";
        Cliente cliente = new Cliente();
        cliente.setEmail(email);

        when(repository.findByEmail(email)).thenReturn(Optional.of(cliente));

        Cliente resultado = service.buscarPorEmail(email);

        assertNotNull(resultado);
        assertEquals(email, resultado.getEmail());
        verify(repository).findByEmail(email);
    }
}