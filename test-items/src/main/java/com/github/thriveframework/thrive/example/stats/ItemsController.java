package com.github.thriveframework.thrive.example.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

import static java.util.stream.Collectors.toList;

@RestController
@Slf4j
public class ItemsController {

    @Autowired
    private WebClient webClient;

    @Autowired
    private ItemRepository repository;

    //todo used for poor mans LB, replace with ribbon
    @Autowired
    private DiscoveryClient discoveryClient;

    //fixme make read stack reactive
    @GetMapping("/api/v1/items")
    public Flux<Item> listItems(){
        return repository.findAll();
    }

    @PostMapping("/api/v1/items")
    public Mono<String> createItem(@RequestBody Item item){
        if (item.getId() == null){
            item.setId(UUID.randomUUID().toString());
        }
        return repository
            .findById(item.getId())
            .flatMap(x ->
                Mono.<String>error(
                    new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Item with ID="+item.getId()+" already exists!")
                )
            ).switchIfEmpty(
                Mono.just(item)
                    .flatMap(i -> repository.save(i))
                    .flatMap( i ->
                        webClient
                            .post()
                            .uri("http://test-stats/api/v1/items/size")
                            .syncBody(item.getSize())
                            .exchange()
                            .flatMap(r -> {
                                if (r.statusCode().is2xxSuccessful())
                                    return Mono.just(r);
                                return Mono.<Item>error(new RuntimeException("Wrong status code! "+r.statusCode()));
                            })
                    )
                    .thenReturn(item.getId())
            );

    }

    @GetMapping("/api/v1/items/{id}")
    public Mono<Item> getItem(@PathVariable String id){
        return repository
            .findById(id)
            .switchIfEmpty(
                Mono.error(
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find item with ID="+id)
                )
            );
    }
}
