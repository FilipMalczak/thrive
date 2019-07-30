package com.github.filipmalczak.thrive.example.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@RestController
@Slf4j
public class ItemsController {
    private Map<String, Item> items = new HashMap<>();

    @Autowired
    private WebClient webClient;

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
        return Mono.just(item)
            .map(i -> {
                items.put(i.getId(), i);
                return i;
            })
            .log("prewebhook")
            .then(
                webClient
                    .post()
                    .uri("http://test-stats/api/v1/items/size")
                    .syncBody(item.getSize())
                    .exchange()
                    .map(r -> {
                        log.info("Status "+r.rawStatusCode());
                        log.info("Headers "+r.headers().asHttpHeaders());;
                        return r;
                    })
            )
            .log("postwebhook")
            .thenReturn(item.getId())
            .log("returned");
    }

    @GetMapping("/api/v1/items/{id}")
    public Item getItem(@PathVariable String id){
        if (items.containsKey(id))
            return items.get(id);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find item with ID="+id);
    }
}
