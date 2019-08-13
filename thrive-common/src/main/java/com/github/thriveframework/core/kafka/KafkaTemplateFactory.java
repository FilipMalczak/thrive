package com.github.thriveframework.core.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaTemplateFactory {
    @Value("${thrive.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${thrive.kafka.max-in-flight:1024}") //todo maybe add "producer" part to prop?
    private int maxInFlight;

    @Autowired
    private ObjectMapper objectMapper;

    //todo by default use span as key, once you introduce distributed tracing
    public <V> KafkaTemplate<String, V> getInstance(Class<V> value){
        return getInstance(String.class, value);
    }

    public <K, V> KafkaTemplate<K, V> getInstance(Class<K> key, Class<V> value){
        return new KafkaTemplate<>(key, value, objectMapper, bootstrapServers, maxInFlight);
    }
}
