package com.github.filipmalczak.thrive.common.service;

import com.github.filipmalczak.thrive.common.swagger.ThriveSwaggerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancerAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
@Import({RibbonAutoConfiguration.class, ReactiveLoadBalancerAutoConfiguration.class, ThriveSwaggerConfig.class})
@Order(Integer.MAX_VALUE-4)
public class ThriveConfig {

//    @ConditionalOnMissingBean
    @Bean
    @Primary
    public WebClient webClient(WebClient.Builder builder, LoadBalancerExchangeFilterFunction lbFunction){
        return builder.filter(lbFunction).build();
    }

}
