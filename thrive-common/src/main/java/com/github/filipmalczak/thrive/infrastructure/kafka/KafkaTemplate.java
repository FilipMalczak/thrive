package com.github.filipmalczak.thrive.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.filipmalczak.thrive.infrastructure.kafka.JsonSupport.*;

@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class KafkaTemplate<K, V> {
    //todo ideas: bound template (with fixed topics), sendValue, sendKey

    private final Class<K> keyClass;
    private final Class<V> valueClass;

    private final ObjectMapper objectMapper;

    private String bootstrapServers;
    private int maxInFlight;

    @Getter(value = AccessLevel.PRIVATE, lazy = true)
    private final KafkaSender<K, V> sender = KafkaSender.create(getSenderOptions());

    private SenderOptions<K, V> getSenderOptions(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        //todo ...Support -> ...?
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonSupport.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSupport.class);
        props.put(JSON_MAPPER_CONFIG, objectMapper);
        props.put(KEY_TYPE_CONFIG, keyClass);
        props.put(VALUE_TYPE_CONFIG, valueClass);
        //todo configure buffer as well
        return SenderOptions.<K, V>create(props).maxInFlight(maxInFlight);
    }


    public Mono<Tuple2<K, V>> send(K key, V value, String... topics) {
        return send(key, value, Stream.of(topics));
    }

    public Mono<Tuple2<K, V>> send(K key, V value, Stream<String> topics) {
        return send(Tuples.of(key, value), topics);
    }

    //fixme we need tuples
    public Mono<Tuple2<K, V>> send(Tuple2<K, V> toSend, Stream<String> topics) {
        Flux<ProducerRecord<K, V>> records = Flux.fromStream(topics)
            .map(t -> new ProducerRecord<>(t, toSend.getT1(), toSend.getT2()));
        return getSender().createOutbound().send(records).then().thenReturn(toSend);
    }
}
