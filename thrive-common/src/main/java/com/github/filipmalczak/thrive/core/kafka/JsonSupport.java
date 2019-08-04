package com.github.filipmalczak.thrive.core.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class JsonSupport<T> implements Serializer<T>, Deserializer<T> {
    public static final String JSON_MAPPER_CONFIG = "json.mapper.instance";
    public static final String KEY_TYPE_CONFIG = "json.mapper.type.key";
    public static final String VALUE_TYPE_CONFIG = "json.mapper.type.value";

    private Class<T> clazz;
    private ObjectMapper mapper;

    @Override
    @SneakyThrows
    public T deserialize(String topic, byte[] data) {
        return mapper.readValue(data, clazz);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        clazz = (Class<T>) configs.get(isKey ? KEY_TYPE_CONFIG : VALUE_TYPE_CONFIG);
        mapper = (ObjectMapper) configs.get(JSON_MAPPER_CONFIG);
    }

    @Override
    @SneakyThrows
    public byte[] serialize(String topic, T data) {
        return mapper.writeValueAsBytes(data);
    }

    @Override
    public void close() {

    }
}
