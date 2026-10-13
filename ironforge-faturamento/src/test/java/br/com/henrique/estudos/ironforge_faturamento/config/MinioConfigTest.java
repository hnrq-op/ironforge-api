package br.com.henrique.estudos.ironforge_faturamento.config;

import io.minio.MinioClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class MinioConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(MinioConfig.class);

    @Test
    @DisplayName("Deve instanciar o Bean do MinioClient com sucesso no contexto do Spring")
    void deveInstanciarMinioClientBeanComSucesso() {
        contextRunner
                .withPropertyValues(
                        "minio.endpoint=http://localhost:9000",
                        "minio.access-key=minioadmin",
                        "minio.secret-key=minioadmin"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(MinioClient.class);
                    assertThat(context.getBean(MinioClient.class)).isNotNull();
                });
    }
}