package br.com.henrique.estudos.ironforge_clientes.validator;

import br.com.henrique.estudos.ironforge_clientes.exceptions.RegistroDuplicadoException;
import br.com.henrique.estudos.ironforge_clientes.model.Cliente;
import br.com.henrique.estudos.ironforge_clientes.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteValidator {

    private final ClienteRepository repository;

    public void validar(Cliente cliente){
        validarCpfUnico(cliente);
        validarEmailUnico(cliente);
        validarKeycloakIdUnico(cliente);
    }


    private void validarCpfUnico(Cliente cliente) {
        repository.findByCpf(cliente.getCpf()).ifPresent(clienteExistente -> {
            if(cliente.getId() == null || !cliente.getId().equals(clienteExistente.getId())){
                throw new RegistroDuplicadoException("O CPF " + cliente.getCpf() + " já está em uso!");
            }
        });
    }

    private void validarEmailUnico(Cliente cliente) {
        repository.findByEmail(cliente.getEmail()).ifPresent(clienteExistente -> {
            if(cliente.getId() == null || !cliente.getId().equals(clienteExistente.getId())){
                throw new RegistroDuplicadoException("O email " + cliente.getEmail() + " já está em uso");
            }
        });
    }

    private void validarKeycloakIdUnico(Cliente cliente) {
        repository.findByKeycloakId(cliente.getKeycloakId()).ifPresent(clienteExistente -> {
            if (cliente.getId() == null || !cliente.getId().equals(clienteExistente.getId())){
                throw new RegistroDuplicadoException("O Keycloak ID " + cliente.getKeycloakId() + " já está em uso");
            }
        });
    }
}
