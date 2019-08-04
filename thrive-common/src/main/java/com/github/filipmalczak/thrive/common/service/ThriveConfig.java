package com.github.filipmalczak.thrive.common.service;

import com.github.filipmalczak.thrive.common.swagger.ThriveSwaggerConfig;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ServerList;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.cloud.zookeeper.discovery.ZookeeperInstance;
import org.springframework.cloud.zookeeper.discovery.ZookeeperRibbonClientConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
@PropertySource(value = "classpath:/META-INF/capabilities.properties", ignoreResourceNotFound = true)
//fixme introduce ribbon - properly!
@Import({
////    DependencyRibbonAutoConfiguration.class,
//    RibbonClientConfiguration.class,
//    RibbonAutoConfiguration.class,
//    ZookeeperConfigBootstrapConfiguration.class,
////    ZookeeperRibbonClientConfiguration.class,
//    ReactiveLoadBalancerAutoConfiguration.class,
    ThriveSwaggerConfig.class
})
@Order(Integer.MAX_VALUE-4)
public class ThriveConfig {

//    @ConditionalOnMissingBean
    @Bean
    @Primary
    public WebClient webClient(WebClient.Builder builder){
//    public WebClient webClient(WebClient.Builder builder, LoadBalancerExchangeFilterFunction lbFunction){
        return builder.build();
    }

//    @ConditionalOnMissingBean
//    @Bean
//    @Primary
//    public ServerList<?> ribbonServerList(IClientConfig config,
//                                          ServiceDiscovery<ZookeeperInstance> serviceDiscovery) {
//        return new ZookeeperRibbonClientConfiguration().ribbonServerList(config, serviceDiscovery);
//    }
}
