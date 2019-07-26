package com.github.filipmalczak.thrive.infrastructure;

import com.github.filipmalczak.thrive.infrastructure.detection.ApiDetector;
import com.github.filipmalczak.thrive.infrastructure.kafka.KafkaTemplate;
import com.github.filipmalczak.thrive.infrastructure.kafka.KafkaTemplateFactory;
import com.github.filipmalczak.thrive.infrastructure.kafka.KafkaTopic;
import com.github.filipmalczak.thrive.infrastructure.kafka.KafkaTopicFactory;
import com.github.filipmalczak.thrive.infrastructure.observing.InstanceNotifier;
import com.github.filipmalczak.thrive.infrastructure.observing.NewInstanceEvent;
import com.github.filipmalczak.thrive.infrastructure.observing.ServiceObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

import static com.github.filipmalczak.thrive.infrastructure.observing.Constants.*;

@Configuration
@EnableConfigurationProperties
@EnableDiscoveryClient
@PropertySource("classpath:/META-INF/build-info.properties")
@PropertySource("classpath:/discovery.properties")
@Slf4j
public class ThriveAutoconfigure {
    //todo handle all thrive-related props and their default, its getting messy
    @Value("${thrive.app-name:${build.artifact:Unknown Thrive app}}")
    private String appName;

    @Autowired
    private Environment environment;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Bean
    public Autowirer autowirer(){
        return new Autowirer(beanFactory);
    }

    @PostConstruct
    void init(){
        log.info(environment.getProperty("build.timestamp"));
        log.info(environment.getProperty("build.time"));
    }

    @ConditionalOnMissingBean
    @Bean
    public WebClient webClient(){
        return WebClient.builder()
            .defaultHeader("X-Clacks-Overhead", "GNU Terry Pratchett")
            .build();
    }

    @ConditionalOnMissingBean
    @Bean
    public ApiDetector apiDetector(WebClient webClient, DiscoveryClient discoveryClient){
        return new ApiDetector(webClient, discoveryClient);
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
