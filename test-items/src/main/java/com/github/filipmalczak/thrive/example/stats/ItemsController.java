package com.github.filipmalczak.thrive.example.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.*;

import static java.util.stream.Collectors.toList;

@RestController
@Slf4j
public class ItemsController {
    private Map<String, Item> items = new HashMap<>();

    @Autowired
    private WebClient webClient;

    //todo used for poor mans LB, replace with ribbon
    @Autowired
    private DiscoveryClient discoveryClient;

    //fixme make read stack reactive
    @GetMapping("/api/v1/items")
    public List<Item> listItems(){
        return items.values().stream().collect(toList());
    }

    @PostMapping("/api/v1/items")
    public Mono<String> createItem(@RequestBody Item item){
        if (item.getId() == null){
            item.setId(UUID.randomUUID().toString());
        } else if (items.containsKey(item.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Item with ID="+item.getId()+" already exists!");
        }
        log.info("WebClient: {}", webClient);
        final String uri = "http://"+ getBaseUrl("test-stats")+"/api/v1/items/size";
        log.info("URI: {}", uri);
        return Mono.just(item)
            .map(i -> {
                items.put(i.getId(), i);
                return i;
            })
            .log("prewebhook")
            .then(
                webClient
                    .post()
                    .uri(uri)
                    .syncBody(item.getSize())
                    .exchange()
                    .map(r -> {
                        log.info("Status "+r.rawStatusCode());
                        log.info("Headers "+r.headers().asHttpHeaders());;
                        if (r.statusCode().is2xxSuccessful())
                            return r;
                        throw new RuntimeException("Wrong status code! "+r.statusCode());
                    })
            )
            .log("postwebhook")
            .thenReturn(item.getId())
            .log("returned");
    }

    private String getBaseUrl(String service){
        List<ServiceInstance> instances = discoveryClient.getInstances(service);
        if (instances.isEmpty())
            throw new RuntimeException("No instance of "+service);
        ServiceInstance instance = instances.get(new Random().nextInt(instances.size()));
        return instance.getHost()+":"+instance.getPort();
    }

    @GetMapping("/api/v1/items/{id}")
    public Item getItem(@PathVariable String id){
        if (items.containsKey(id))
            return items.get(id);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find item with ID="+id);
    }
}
