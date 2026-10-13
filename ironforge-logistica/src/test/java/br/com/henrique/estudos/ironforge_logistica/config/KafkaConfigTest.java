package br.com.henrique.estudos.ironforge_logistica.config;

import br.com.henrique.estudos.ironforge_logistica.publisher.representation.PedidoEnviadoEventRepresentationDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class KafkaConfigTest {

    private KafkaConfig kafkaConfig;
    private static final String BOOTSTRAP_SERVERS = "localhost:29092";

    @BeforeEach
    void setUp() {
        kafkaConfig = new KafkaConfig();
        ReflectionTestUtils.setField(kafkaConfig, "bootstrapsServers", BOOTSTRAP_SERVERS);
    }

    @Test
    @DisplayName("producerFactory - Deve instanciar e configurar o ProducerFactory com as propriedades corretas")
    void deveCriarProducerFactoryComConfiguracoesCorretas() {
        ProducerFactory<String, PedidoEnviadoEventRepresentationDTO> factory = kafkaConfig.producerFactory();

        assertNotNull(factory);
        assertTrue(factory instanceof DefaultKafkaProducerFactory);

        Map<String, Object> configs = factory.getConfigurationProperties();
        assertEquals(BOOTSTRAP_SERVERS, configs.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(StringSerializer.class, configs.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(JacksonJsonSerializer.class, configs.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }

    @Test
    @DisplayName("kafkaTemplate - Deve criar instância de KafkaTemplate associada ao ProducerFactory")
    void deveCriarKafkaTemplateComSucesso() {
        KafkaTemplate<String, PedidoEnviadoEventRepresentationDTO> template = kafkaConfig.kafkaTemplate();

        assertNotNull(template);
        assertNotNull(template.getProducerFactory());
    }
}