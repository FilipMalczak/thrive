package com.github.filipmalczak.thrive.admin;

import com.github.filipmalczak.thrive.ThriveService;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;

@EnableAdminServer
@ThriveService
public class ThriveAdminApp {
    public static void main(String[] args) {
        SpringApplication.run(ThriveAdminApp.class, args);
    }
}
