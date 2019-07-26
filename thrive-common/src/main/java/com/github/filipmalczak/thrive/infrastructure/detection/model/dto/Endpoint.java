package com.github.filipmalczak.thrive.infrastructure.detection.model.dto;

import lombok.*;
import org.springframework.http.HttpMethod;

import java.util.Optional;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Endpoint {
    //todo - replace with synthethic getter and ThriveInstance
    String serviceAddress;
    Optional<HttpMethod> method;
    String path;

    public Endpoint(String serviceAddress, @NonNull HttpMethod method, String path) {
        this.serviceAddress = serviceAddress;
        this.method = Optional.of(method);
        this.path = path;
    }

    public Endpoint(String serviceAddress, String path) {
        this.serviceAddress = serviceAddress;
        this.method = Optional.empty();
        this.path = path;
    }
}
