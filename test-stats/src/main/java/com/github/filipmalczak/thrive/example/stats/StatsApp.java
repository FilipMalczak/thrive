package com.github.filipmalczak.thrive.example.stats;

import com.github.filipmalczak.thrive.ThrivingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

@ThrivingService
@Slf4j
public class StatsApp {
    public static void main(String[] args){
        SpringApplication.run(StatsApp.class, args);
    }
}
