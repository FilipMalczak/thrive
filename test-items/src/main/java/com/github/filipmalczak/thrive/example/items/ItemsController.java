package com.github.filipmalczak.thrive.example.items;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
public class ItemsController {
    private Map<String, Item> items = new HashMap<>();

    @GetMapping("/api/v1/items")
    public List<Item> listItems(){
        return items.values().stream().collect(toList());
    }

    @PostMapping("/api/v1/items")
    public String createItem(@RequestBody Item item){
        if (item.getId() == null){
            item.setId(UUID.randomUUID().toString());
        } else if (items.containsKey(item.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Item with ID="+item.getId()+" already exists!");
        }
        items.put(item.getId(), item);
        return item.getId();
    }

    @GetMapping("/api/v1/items/{id}")
    public Item getItem(@PathVariable String id){
        if (items.containsKey(id))
            return items.get(id);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find item with ID="+id);
    }
}
