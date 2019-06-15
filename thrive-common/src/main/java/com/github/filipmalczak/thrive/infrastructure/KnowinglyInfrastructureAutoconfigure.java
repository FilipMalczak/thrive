package com.github.filipmalczak.thrive.infrastructure;

import com.github.filipmalczak.thrive.infrastructure.detection.ApiDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties
@EnableDiscoveryClient
@PropertySource("classpath:/META-INF/build-info.properties")
@PropertySource("classpath:/discovery.properties")
@Slf4j
public class KnowinglyInfrastructureAutoconfigure {
    @Value("${thrive.app-name:${build.artifact:Unknown Knowingly infrastructure app}}")
    private String appName;

    @Autowired
    private Environment environment;

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
}
