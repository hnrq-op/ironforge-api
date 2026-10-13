package br.com.henrique.estudos.ironforge_pedidos.config;

import br.com.henrique.estudos.ironforge_pedidos.publisher.representation.PedidoPagoEventRepresentationDTO;
import br.com.henrique.estudos.ironforge_pedidos.subscriber.representation.PedidoEnviadoEventRepresentation;
import br.com.henrique.estudos.ironforge_pedidos.subscriber.representation.PedidoFaturadoEventRepresentation;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ProducerFactory<String, PedidoPagoEventRepresentationDTO> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, PedidoPagoEventRepresentationDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PedidoFaturadoEventRepresentation> faturadoContainerFactory() {
        JacksonJsonDeserializer<PedidoFaturadoEventRepresentation> deserializer =
                new JacksonJsonDeserializer<>(PedidoFaturadoEventRepresentation.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        DefaultKafkaConsumerFactory<String, PedidoFaturadoEventRepresentation> consumerFactory =
                new DefaultKafkaConsumerFactory<>(
                        consumerProps(),
                        new StringDeserializer(),
                        new ErrorHandlingDeserializer<>(deserializer)
                );

        ConcurrentKafkaListenerContainerFactory<String, PedidoFaturadoEventRepresentation> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PedidoEnviadoEventRepresentation> enviadoContainerFactory() {
        JacksonJsonDeserializer<PedidoEnviadoEventRepresentation> deserializer =
                new JacksonJsonDeserializer<>(PedidoEnviadoEventRepresentation.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        DefaultKafkaConsumerFactory<String, PedidoEnviadoEventRepresentation> consumerFactory =
                new DefaultKafkaConsumerFactory<>(
                        consumerProps(),
                        new StringDeserializer(),
                        new ErrorHandlingDeserializer<>(deserializer)
                );

        ConcurrentKafkaListenerContainerFactory<String, PedidoEnviadoEventRepresentation> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}