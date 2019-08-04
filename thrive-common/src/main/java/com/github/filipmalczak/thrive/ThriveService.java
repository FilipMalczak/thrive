package com.github.filipmalczak.thrive;

import com.github.filipmalczak.thrive.core.ThriveCoreConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.lang.annotation.*;

/**
 * Annotation similiar to {@link SpringCloudApplication} or
 * {@link org.springframework.boot.autoconfigure.SpringBootApplication SpringBootApplication}
 * that indicates that a class is a main class for Thrive daemon service (admin, gateway, docs, etc - all the
 * infrastructural stuff, "thrive service" in short).
 * <p>
 * Provides cloud-related features, configuration properties, discovery client, lots of useful beans (Kafka template
 * and topic factories, non-load-balanced {@link org.springframework.web.reactive.function.client.WebClient WebClient}
 * (available with {@link com.github.filipmalczak.thrive.core.SimpleWebClient SimpleWebClient} qualifier),
 * enables propagation of service lifecycle events via Kafka (and that enables instance notifier, service observer and
 * API detector) and loads some build- and discovery-related properties.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringCloudApplication
@EnableConfigurationProperties
@EnableDiscoveryClient
@Import(ThriveCoreConfig.class)
public @interface ThriveService {
}
