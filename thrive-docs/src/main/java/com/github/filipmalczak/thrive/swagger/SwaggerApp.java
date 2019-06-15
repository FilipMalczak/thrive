package com.github.filipmalczak.thrive.swagger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@Slf4j
@EnableWebFlux
public class SwaggerApp {
    @Autowired
    private Environment environment;

    public static void main(String[] args){
        SpringApplication.run(SwaggerApp.class, args);
    }
}
