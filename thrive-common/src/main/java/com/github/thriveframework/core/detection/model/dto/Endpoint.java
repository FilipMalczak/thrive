package com.github.thriveframework.core.detection.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpMethod;

import java.util.Optional;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Endpoint {
    ThriveInstance instance;
    Optional<HttpMethod> method;
    String path;

    public Endpoint(ThriveInstance instance, @NonNull HttpMethod method, String path) {
        this.instance = instance;
        this.method = Optional.of(method);
        this.path = path;
    }

    public Endpoint(ThriveInstance instance, String path) {
        this.instance = instance;
        this.method = Optional.empty();
        this.path = path;
    }

    public String getServiceName() {
        return instance.getName();
    }

    public String getServiceAddress() {
        return instance.getAddress();
    }
}
