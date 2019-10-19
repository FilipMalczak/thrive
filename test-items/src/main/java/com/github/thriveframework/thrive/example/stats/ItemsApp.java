package com.github.thriveframework.thrive.example.stats;

import com.github.thriveframework.ThrivingService;
import com.github.thriveframework.support.mongo.ThiveMongoReactiveSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

@ThrivingService
@ThiveMongoReactiveSupport
@Slf4j
public class ItemsApp {
    public static void main(String[] args){
        SpringApplication.run(ItemsApp.class, args);
    }
}
