package com.github.thriveframework.thrive.example.stats;

import com.github.thriveframework.ThrivingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

@ThrivingService
@Slf4j
public class StatsApp {
    public static void main(String[] args){
        SpringApplication.run(StatsApp.class, args);
    }
}
