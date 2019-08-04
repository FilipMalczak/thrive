package com.github.filipmalczak.thrive.core.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.HashMap;
import java.util.Map;

import static com.github.filipmalczak.thrive.core.kafka.JsonSupport.*;
import static java.util.Arrays.asList;

@AllArgsConstructor
@Builder
public class KafkaTopic<K, V> {
    private String topicName;
    private Class<K> keyClass;
    private Class<V> valueClass;

    private String bootstrapServers;
    private ObjectMapper mapper;

    private String groupId;
    private String clientId;

    private Map<String, Object> getReceiverProperties(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, JsonSupport.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSupport.class);
        props.put(JSON_MAPPER_CONFIG, mapper);
        props.put(KEY_TYPE_CONFIG, keyClass);
        props.put(VALUE_TYPE_CONFIG, valueClass);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        return props;
    }

    private ReceiverOptions<K, V> getReceiverOptions(){
        return ReceiverOptions.<K, V>create(getReceiverProperties()).subscription(asList(topicName));
    }

    private KafkaReceiver<K, V> getReceiver(){
        return KafkaReceiver.create(getReceiverOptions());
    }


    public Flux<Tuple2<K, V>> listen(){
        return getReceiver().receiveAutoAck().flatMap(f ->
                f.map( r ->
                    Tuples.of(r.key(), r.value())
                )
            );
    }

    public Flux<K> listenToKeys(){
        return listen().map(Tuple2::getT1);
    }

    public Flux<V> listenToValues(){
        return listen().map(Tuple2::getT2);
    }
}
