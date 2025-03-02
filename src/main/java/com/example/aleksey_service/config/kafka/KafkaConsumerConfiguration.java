package com.example.aleksey_service.config.kafka;

import com.example.aleksey_service.deserializer.MessageDeserializer;
import com.example.aleksey_service.dto.KafkaDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConsumerConfiguration {

    @Value("${kafka.consumer-group-id}")
    private String groupId;

    @Value("${kafka.bootstrap-servers}")
    private String servers;

    @Value("${kafka.session-timeout-ms}")
    private String sessionTimeoutMs;

    @Value("${kafka.max-partition-fetch-bytes}")
    private String maxPartitionFetchBytes;

    @Value("${kafka.max-poll-records}")
    private String maxPollRecords;

    @Value("${kafka.max-poll-interval-ms}")
    private String maxPollIntervalMs;


    /**
     * Конфигурация ConsumerFactory для KafkaDto
     */
    @Bean
    public ConsumerFactory<String, KafkaDto> taskConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MessageDeserializer.class.getName());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, KafkaDto.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.aleksey_service.dto");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(KafkaDto.class)
        );
    }

    /**
     * KafkaListenerContainerFactory с обработкой ошибок
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaDto> taskKafkaListenerContainerFactory(
            @Qualifier("taskConsumerFactory") ConsumerFactory<String, KafkaDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, KafkaDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(5000);
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    /**
     * Обработчик ошибок Kafka
     */
    private CommonErrorHandler errorHandler() {
        DefaultErrorHandler handler = new DefaultErrorHandler(new FixedBackOff(1000, 3));
        handler.addNotRetryableExceptions(IllegalStateException.class);
        handler.setRetryListeners((record, ex, deliveryAttempt) ->
                log.error("Ошибка обработки сообщения offset={} (попытка={}) : {}", record.offset(), deliveryAttempt, ex.getMessage())
        );
        return handler;
    }
}