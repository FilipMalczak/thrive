package com.github.thriveframework.core;

import com.github.thriveframework.core.detection.ApiDetector;
import com.github.thriveframework.core.kafka.KafkaTemplate;
import com.github.thriveframework.core.kafka.KafkaTemplateFactory;
import com.github.thriveframework.core.kafka.KafkaTopic;
import com.github.thriveframework.core.kafka.KafkaTopicFactory;
import com.github.thriveframework.core.observing.InstanceNotifier;
import com.github.thriveframework.core.observing.NewInstanceEvent;
import com.github.thriveframework.core.observing.ServiceObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.thriveframework.core.observing.Constants.*;

@Configuration
@Slf4j
@PropertySource({
    "classpath:/git.properties",
    "classpath:/META-INF/build-info.properties",
    "classpath:/discovery.properties",
    "classpath:/thrive-defaults.properties",
    "classpath:/spring-defaults.properties"
})
@Order(Integer.MAX_VALUE-5)
public class ThriveCoreConfig {
    //todo handle all thrive-related props and their default, its getting messy
    @Value("${thrive.app-name:${build.artifact:Unknown Thrive app}}")
    private String appName;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Bean
    public Autowirer autowirer(){
        return new Autowirer(beanFactory);
    }

    @ConditionalOnMissingBean
    @Bean
//    @LoadBalanced
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder()
            .defaultHeader("X-Clacks-Overhead", "GNU Terry Pratchett");
    }

    @Bean
    @SimpleWebClient
    public WebClient simpleWebClient(WebClient.Builder builder){
        return builder.build();
    }

    @ConditionalOnMissingBean
    @Bean
    public ApiDetector apiDetector(){
        return autowirer().autowired(ApiDetector.class);
    }

    //fixme these should be somehow conditional; probably should be disable-able with props
    @Bean
    public ServiceObserver serviceObserver(){
        return autowirer().autowired(ServiceObserver.class);
    }

    @Bean
    public InstanceNotifier instanceNotifier(){
        return autowirer().autowired(InstanceNotifier.class);
    }

    @ConditionalOnMissingBean
    @Bean
    public KafkaTemplateFactory kafkaTemplateFactory(){
        return autowirer().autowired(KafkaTemplateFactory.class);
    }

    @ConditionalOnMissingBean
    @Bean
    public KafkaTopicFactory kafkaTopicFactory(){
        return autowirer().autowired(KafkaTopicFactory.class);
    }

    @ConditionalOnMissingBean(name = KAFKA_TOPIC_BEAN)
    @Bean(name = KAFKA_TOPIC_BEAN)
    public KafkaTopic<String, NewInstanceEvent> serviceObserverKafkaTopic(){
        log.info("Providing observer topic");
        return kafkaTopicFactory().getInstance(KAFKA_TOPIC_NAME, NewInstanceEvent.class);
    }

    @ConditionalOnMissingBean(name = KAFKA_TEMPLATE_BEAN)
    @Bean(name = KAFKA_TEMPLATE_BEAN)
    public KafkaTemplate<String, NewInstanceEvent> instanceNotifierKafkaTemplate(){
        log.info("Providing notifier template");
        return kafkaTemplateFactory().getInstance(NewInstanceEvent.class);
    }
}
