package br.com.henrique.estudos.ironforge_pedidos.client;

import br.com.henrique.estudos.ironforge_pedidos.config.ClientsConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ProdutosClientTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(ClientsConfig.class)
            .withPropertyValues(
                    "ironforge.pedidos.clients.clientes.url=http://localhost:8082",
                    "ironforge.pedidos.clients.produtos.url=http://localhost:8081"
            );

    @Test
    @DisplayName("obterDetalhes - Deve carregar o bean de ProdutosClient corretamente no contexto")
    void obterDetalhes_DeveCarregarBeanProdutosClient() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ProdutosClient.class);
        });
    }
}