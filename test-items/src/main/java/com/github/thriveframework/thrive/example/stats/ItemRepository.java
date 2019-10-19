package com.github.thriveframework.thrive.example.stats;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends ReactiveMongoRepository<Item, String> {
}
