package com.github.thriveframework.core.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class KafkaTopicFactory {
    @Value("${thrive.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Autowired
    private ObjectMapper mapper;

    @Value("${thrive.kafka.groupId:${build.group:thrive-default}}")
    private String groupId;

    //fixme what should have precedence? build name or artifact?
    @Value("${thrive.kafka.clientId:${build.name:${build.artifact:unknown}}}")
    private String clientId;

    public <K, V> KafkaTopic<K, V> getInstance(String topic, Class<K> key, Class<V> value){
        return new KafkaTopic<>(topic, key, value, bootstrapServers, mapper, groupId, clientId);
    }

    //fixme relates to template factory
    public <V> KafkaTopic<String, V> getInstance(String topic, Class<V> value){
        return getInstance(topic, String.class, value);
    }
}
