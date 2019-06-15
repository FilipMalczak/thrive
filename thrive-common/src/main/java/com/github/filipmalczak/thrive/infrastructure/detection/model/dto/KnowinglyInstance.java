package com.github.filipmalczak.thrive.infrastructure.detection.model.dto;

import lombok.Value;
import lombok.experimental.Accessors;

import java.util.Set;

@Value
public class KnowinglyInstance {
    String address;
    @Accessors(fluent = true)
    boolean hasApi;
    @Accessors(fluent = true)
    boolean hasSwagger;
    Set<String> websocketPaths;

    public boolean hasWebsocket(){
        return !websocketPaths.isEmpty();
    }
}
