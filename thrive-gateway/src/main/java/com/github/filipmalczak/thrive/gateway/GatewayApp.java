package com.github.filipmalczak.thrive.gateway;

import com.github.filipmalczak.thrive.infrastructure.observing.ServiceObserver;
import com.github.filipmalczak.thrive.infrastructure.detection.ApiDetector;
import com.github.filipmalczak.thrive.infrastructure.detection.model.dto.Endpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.logging.Level;

@SpringBootApplication
@Slf4j
public class GatewayApp {
    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private RouteLocator locator;

    @Autowired
    private ServiceObserver observer;


    public static void main(String[] args){
        SpringApplication.run(GatewayApp.class, args);
    }

    @PostConstruct
    public void init(){
        observer.watchChanges().doOnEach(x -> listen());
    }

//    @EventListener(InstanceRegisteredEvent.class)
    @Scheduled(fixedDelay = 5 * 60 * 1000, initialDelay = 5 * 1000)
    public void listen(){
        ((CachingRouteLocator)locator).refresh();
    }



    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder, ApiDetector apiDetector) {
        return () -> apiDetector
            .findEndpoints()
            .log("Detected endpoints", Level.FINE)
            .reduce(
                builder.routes(),
                (b, e) -> b.route(r -> routeEndpoint(r, e))
            )
            .map(RouteLocatorBuilder.Builder::build)
            .flux()
            .flatMap(RouteLocator::getRoutes);
    }

    private Route.AsyncBuilder routeEndpoint(PredicateSpec r, Endpoint e){
        return e.getMethod()
            .map(m -> r
                .method(m)
                .and()
                .path(e.getPath())
                .uri(e.getServiceAddress())
            ).orElseGet(() -> r
                .path(e.getPath())
                .uri(e.getServiceAddress())
            );
    }

    @Bean
    public RouteLocator docsRouteLocator(RouteLocatorBuilder builder){
        return builder.routes()
            .route(r -> r
                .path("/docs/**")
                .uri("http://docs:8080")
            )
            .build();
    }

    @Bean
    public RouteLocator swaggerRouteLocator(RouteLocatorBuilder builder){
        return builder.routes()
            .route(r -> r
                .path("/swagger-ui.html")
                .filters(f -> f.setPath("/"))
                .uri("http://swaggerui:8080/")
            )
            .route(r -> r
                .path("/swagger-ui*")
                .uri("http://swaggerui:8080")
            )
            .build();
    }
}
