package com.github.filipmalczak.thrive.example.items;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperAutoServiceRegistration;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@PropertySource("classpath:/META-INF/build-info.properties")
@PropertySource("classpath:/discovery.properties")
@PropertySource("classpath:/git.properties")
@PropertySource(value = "classpath:/META-INF/capabilities.properties", ignoreResourceNotFound = true)
@Slf4j
public class ItemsApp {

    @Bean
    public WebClient webClient(){
        return WebClient.builder()
            .defaultHeader("X-Clacks-Overhead", "GNU Terry Pratchett")
            .build();
    }


    public static void main(String[] args){
        SpringApplication.run(ItemsApp.class, args);
    }

    //fixme why does it work here and not in autoconfig?

}
