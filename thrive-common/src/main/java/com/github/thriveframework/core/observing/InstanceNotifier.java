package com.github.thriveframework.core.observing;

import com.github.thriveframework.core.kafka.KafkaTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.discovery.watcher.DependencyState;
import org.springframework.context.event.EventListener;

@Slf4j
//todo InfrastructureNotifier?
public class InstanceNotifier {

    @Value("${thrive.app-name:${build.artifact:Thrive app}}")
    private String serviceName;

    //fixme
    @Value("${server.port}")
    private int port;

    @Autowired
    private KafkaTemplate<String, NewInstanceEvent> notificationsTemplate;

    @EventListener(InstanceRegisteredEvent.class)
    public void listen(InstanceRegisteredEvent<ZookeeperDiscoveryProperties> e){
        log.info("Service name: "+serviceName);
        log.info("Located at: "+e.getConfig().getInstanceHost()+":"+port);
        notificationsTemplate
            .send(serviceName, new NewInstanceEvent(serviceName, DependencyState.CONNECTED), Constants.KAFKA_TOPIC_NAME)
            .log("notification")
            .block();
    }
}
