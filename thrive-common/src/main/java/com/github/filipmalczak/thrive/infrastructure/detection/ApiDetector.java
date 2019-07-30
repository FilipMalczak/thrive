package com.github.filipmalczak.thrive.infrastructure.detection;

import com.github.filipmalczak.thrive.infrastructure.SimpleWebClient;
import com.github.filipmalczak.thrive.infrastructure.detection.model.dto.Endpoint;
import com.github.filipmalczak.thrive.infrastructure.detection.model.dto.ThriveInstance;
import com.github.filipmalczak.thrive.infrastructure.detection.model.http.SwaggerDocs;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class ApiDetector {
    @Autowired
    @SimpleWebClient
    private WebClient webClient;

    @Autowired
    private DiscoveryClient discoveryClient;


    public static final String CAPABILITY_PREFIX = "capability.";
    public static final String API_CAPABILITY = CAPABILITY_PREFIX+"api";
    public static final String SWAGGER_CAPABILITY = CAPABILITY_PREFIX+"swagger";
    public static final String WEBSOCKET_CAPABILITY = CAPABILITY_PREFIX+"websocket";

    public Flux<Endpoint> findEndpoints(){
        return getInstances().filter(ThriveInstance::hasApi).flatMap(this::extractEndpoints);
    }

    public Flux<ThriveInstance> getInstances(){
        return Flux.fromStream(
                discoveryClient
                    .getServices()
                    .stream()
                    .flatMap(sid ->
                        discoveryClient.getInstances(sid).stream()
                    )
            )
            .log("raw instances")
            .map(this::translate)
            .log("translated instances");
    }

    private ThriveInstance translate(ServiceInstance instance){
        return new ThriveInstance(
            instance.getServiceId(),
    Optional.ofNullable(instance.getScheme()).orElse("http")
                +"://"
                +instance.getHost()
                +":"
                +instance.getPort(),
            instance.getMetadata().containsKey(API_CAPABILITY),
            instance.getMetadata().containsKey(SWAGGER_CAPABILITY),
            parseWsPaths(instance.getMetadata().getOrDefault(WEBSOCKET_CAPABILITY, ""))
        );
    }

    private Set<String> parseWsPaths(String capability){
        Set<String> result = new HashSet<>(asList(capability.split(";")));
        result.remove("");
        return result;
    }

    public Flux<Endpoint> extractEndpoints(ThriveInstance instance){
        Flux<Endpoint> result = Flux.empty();
        if (instance.hasSwagger())
            result = result.thenMany(endpointsFromSwagger(instance));
        if (instance.hasWebsocket())
            result = result.thenMany(endpointsFromWs(instance));
        return result;

    }

    private Flux<Endpoint> endpointsFromSwagger(ThriveInstance instance){
        return webClient
            .get()
            .uri(instance.getAddress()+"/v2/api-docs?group=api")
            .retrieve()
            .bodyToMono(SwaggerDocs.class)
            .flux()
            .flatMap(docs ->
                Flux.fromIterable(docs.getPaths().entrySet())
                    .flatMap(e ->
                        Flux.fromIterable(e.getValue().entrySet())
                            .map(e2 ->
                                new Endpoint(
                                    instance,
                                    HttpMethod.valueOf(e2.getKey().toUpperCase()),
                                    e.getKey()
                                )
                            )
                    )

            );
    }

    private Flux<Endpoint> endpointsFromWs(ThriveInstance instance){
        return Flux.fromIterable(instance.getWebsocketPaths())
            .map(path -> new Endpoint(instance, path));
    }
}
