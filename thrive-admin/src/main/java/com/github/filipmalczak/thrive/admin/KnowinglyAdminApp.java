package com.github.filipmalczak.thrive.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableAdminServer
@SpringBootApplication
public class KnowinglyAdminApp {
    public static void main(String[] args) {
        SpringApplication.run(KnowinglyAdminApp.class, args);
    }
}
