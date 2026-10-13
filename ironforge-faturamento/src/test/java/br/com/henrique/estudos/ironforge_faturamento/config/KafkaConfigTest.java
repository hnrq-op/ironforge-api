package br.com.henrique.estudos.ironforge_faturamento.config;

import br.com.henrique.estudos.ironforge_faturamento.publisher.representation.PedidoFaturadoEventRepresentationDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(KafkaConfig.class)
            .withPropertyValues("spring.kafka.bootstrap-servers=localhost:9092");

    @Test
    @DisplayName("Deve carregar os beans de Kafka ProducerFactory e KafkaTemplate corretamente")
    void deveCarregarBeansDoKafka() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ProducerFactory.class);
            assertThat(context).hasSingleBean(KafkaTemplate.class);

            @SuppressWarnings("unchecked")
            ProducerFactory<String, PedidoFaturadoEventRepresentationDTO> factory =
                    context.getBean(ProducerFactory.class);

            assertThat(factory).isInstanceOf(DefaultKafkaProducerFactory.class);

            DefaultKafkaProducerFactory<String, PedidoFaturadoEventRepresentationDTO> defaultFactory =
                    (DefaultKafkaProducerFactory<String, PedidoFaturadoEventRepresentationDTO>) factory;

            assertThat(defaultFactory.getConfigurationProperties())
                    .containsEntry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
                    .containsEntry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                    .containsEntry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        });
    }
}