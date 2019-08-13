package com.github.thriveframework.core.observing;

import com.github.thriveframework.core.kafka.KafkaTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Flux;

import static com.github.thriveframework.core.observing.Constants.KAFKA_TOPIC_BEAN;

public class ServiceObserver {


    @Autowired
    @Qualifier(KAFKA_TOPIC_BEAN)
    private KafkaTopic<String, NewInstanceEvent> kafkaTopic;

    public Flux<NewInstanceEvent> watchChanges() {
        return kafkaTopic.listenToValues().log("watched");
    }
}
