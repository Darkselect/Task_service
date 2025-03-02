package com.example.aleksey_service.config.kafka;

import com.example.aleksey_service.dto.KafkaDto;
import com.example.aleksey_service.kafka.KafkaTaskProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaProducerConfiguration {
    @Value("${kafka.bootstrap-servers}")
    private String servers;

    @Value("${kafka.task-topic}")
    private String taskTopic;

    /**
     * ProducerFactory для TaskDto
     */
    @Bean
    public ProducerFactory<String, KafkaDto> taskProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * KafkaTemplate для TaskDto
     */
    @Bean("emailKafkaTemplate")
    public KafkaTemplate<String, KafkaDto> taskKafkaTemplate(@Qualifier("taskProducerFactory") ProducerFactory<String, KafkaDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * KafkaTaskProducer с условием включения
     */
    @Bean
    @ConditionalOnProperty(value = "spring.kafka.producer.enable", havingValue = "true", matchIfMissing = true)
    public KafkaTaskProducer taskProducer(@Qualifier("emailKafkaTemplate") KafkaTemplate<String, KafkaDto> template) {
        template.setDefaultTopic(taskTopic);
        return new KafkaTaskProducer(template);
    }
}
