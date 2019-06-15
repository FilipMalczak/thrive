package com.github.filipmalczak.thrive.infrastructure.detection.model.http;

import lombok.Data;

import java.util.Map;

@Data
public class SwaggerDocs {
    public static class Dummy {}

    private Map<String, Map<String, Dummy>> paths;
}