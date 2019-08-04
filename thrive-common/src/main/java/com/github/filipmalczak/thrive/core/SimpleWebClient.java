package com.github.filipmalczak.thrive.core;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

import static com.github.filipmalczak.thrive.core.SimpleWebClient.SIMPLE_WEB_CLIENT_NAME;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier(SIMPLE_WEB_CLIENT_NAME)
public @interface SimpleWebClient {
    String SIMPLE_WEB_CLIENT_NAME = "simpleWebClient";
}
