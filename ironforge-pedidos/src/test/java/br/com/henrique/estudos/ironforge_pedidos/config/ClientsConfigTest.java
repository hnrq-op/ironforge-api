package br.com.henrique.estudos.ironforge_pedidos.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ClientsConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(ClientsConfig.class)
            .withPropertyValues(
                    "ironforge.pedidos.clients.clientes.url=http://localhost:8082",
                    "ironforge.pedidos.clients.produtos.url=http://localhost:8081"
            );

    @Test
    @DisplayName("Deve carregar a classe de configuração ClientsConfig no contexto")
    void deveCarregarClientsConfig() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ClientsConfig.class);
        });
    }
}