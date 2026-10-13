package br.com.henrique.estudos.ironforge_clientes.service;

import br.com.henrique.estudos.ironforge_clientes.controller.dto.endereco.EnderecoRequestDTO;
import br.com.henrique.estudos.ironforge_clientes.mapper.EnderecoMapper;
import br.com.henrique.estudos.ironforge_clientes.model.Endereco;
import br.com.henrique.estudos.ironforge_clientes.repository.EnderecoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnderecoServiceTest {

    @InjectMocks
    private EnderecoService service;

    @Mock
    private EnderecoRepository repository;

    @Mock
    private EnderecoMapper mapper;

    @Test
    @DisplayName("SALVAR - Deve salvar o endereço com sucesso")
    void deveSalvarEnderecoComSucesso() {
        Endereco endereco = new Endereco();

        service.salvar(endereco);

        verify(repository, times(1)).save(endereco);
    }

    @Test
    @DisplayName("ATUALIZAR - Deve atualizar o endereço com sucesso quando o ID existir")
    void deveAtualizarEnderecoComSucesso() {
        UUID id = UUID.randomUUID();
        Endereco enderecoExistente = new Endereco();
        enderecoExistente.setId(id);
        EnderecoRequestDTO dto = mock(EnderecoRequestDTO.class);

        when(repository.findById(id)).thenReturn(Optional.of(enderecoExistente));

        service.atualizar(id, dto);

        verify(repository, times(1)).findById(id);
        verify(mapper, times(1)).updateEntityFromDto(dto, enderecoExistente);
    }

    @Test
    @DisplayName("ATUALIZAR - Deve lançar IllegalArgumentException quando o ID for nulo")
    void deveLancarExcecaoAoAtualizarComIdNulo() {
        EnderecoRequestDTO dto = mock(EnderecoRequestDTO.class);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.atualizar(null, dto)
        );

        assertEquals("Para atualizar é necessário que o endereço esteja salvo na base (ID não pode ser nulo)", exception.getMessage());
        verifyNoInteractions(repository, mapper);
    }

    @Test
    @DisplayName("ATUALIZAR - Deve lançar EntityNotFoundException quando endereço não existir")
    void deveLancarExcecaoAoAtualizarEnderecoInexistente() {
        UUID id = UUID.randomUUID();
        EnderecoRequestDTO dto = mock(EnderecoRequestDTO.class);

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.atualizar(id, dto));

        verify(repository).findById(id);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("DELETAR - Deve deletar o endereço com sucesso quando o ID existir")
    void deveDeletarEnderecoComSucesso() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.deletar(id);

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("DELETAR - Deve lançar IllegalArgumentException quando o ID for nulo")
    void deveLancarExcecaoAoDeletarComIdNulo() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.deletar(null)
        );

        assertEquals("Para deletar é necessário que o endereço esteja salvo na base (ID não pode ser nulo)", exception.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("DELETAR - Deve lançar EntityNotFoundException quando o ID não existir")
    void deveLancarExcecaoAoDeletarIdInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.deletar(id)
        );

        assertEquals("Endereço não encontrado para deletar.", exception.getMessage());
        verify(repository).existsById(id);
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("BUSCAR POR ID - Deve retornar Endereco quando encontrado por ID")
    void deveBuscarPorIdComSucesso() {
        UUID id = UUID.randomUUID();
        Endereco endereco = new Endereco();
        endereco.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(endereco));

        Endereco resultado = service.buscarPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        verify(repository).findById(id);
    }

    @Test
    @DisplayName("BUSCAR POR ID - Deve lançar EntityNotFoundException quando ID não encontrado")
    void deveLancarExcecaoAoBuscarPorIdInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.buscarPorId(id));
        verify(repository).findById(id);
    }

    @Test
    @DisplayName("BUSCAR POR CLIENTE ID - Deve retornar Endereco quando encontrado por cliente ID")
    void deveBuscarPorClienteIdComSucesso() {
        UUID clienteId = UUID.randomUUID();
        Endereco endereco = new Endereco();

        when(repository.findByClienteId(clienteId)).thenReturn(Optional.of(endereco));

        Endereco resultado = service.buscarPorClienteId(clienteId);

        assertNotNull(resultado);
        verify(repository).findByClienteId(clienteId);
    }

    @Test
    @DisplayName("BUSCAR POR CLIENTE ID - Deve lançar EntityNotFoundException quando cliente ID não encontrado")
    void deveLancarExcecaoAoBuscarPorClienteIdInexistente() {
        UUID clienteId = UUID.randomUUID();
        when(repository.findByClienteId(clienteId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.buscarPorClienteId(clienteId));
        verify(repository).findByClienteId(clienteId);
    }
}