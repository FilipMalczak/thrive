package com.github.filipmalczak.thrive;

import com.github.filipmalczak.thrive.common.service.ThriveConfig;
import org.springframework.cloud.zookeeper.discovery.ZookeeperRibbonClientConfiguration;
import org.springframework.cloud.zookeeper.discovery.dependency.DependencyRibbonAutoConfiguration;
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.lang.annotation.*;

/**
 * Extension of {@link ThriveService} that marks services holding business value. Such services are called "thriving
 * services".
 * <p>
 * Every thriving service has all the features of thrive service and some more - they come with preconfigured
 * Ribbon-enabled {@link org.springframework.web.reactive.function.client.WebClient WebClient} and in the future
 * preconfigured MongoDB and PostgreSQL.
 * <p>
 * Keep in mind, that this does not enable WebFlux, so you'll need to use
 * {@link org.springframework.web.reactive.config.EnableWebFlux EnableWebFlux} manually.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ThriveService
@Import({ThriveConfig.class, })
public @interface ThrivingService {
}
