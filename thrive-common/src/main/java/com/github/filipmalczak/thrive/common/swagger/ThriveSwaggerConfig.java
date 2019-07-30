package com.github.filipmalczak.thrive.common.swagger;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

@Configuration
@EnableSwagger2WebFlux
public class ThriveSwaggerConfig {
    public static final Predicate<String> PUBLIC_API_PATHS = PathSelectors.regex("/api[/]?.*");
    public static final Predicate<String> INTERNAL_API_PATHS = PathSelectors.regex("/internal[/]?.*");
    public static final Predicate<String> TECHNICAL_API_PATHS = PUBLIC_API_PATHS.or(INTERNAL_API_PATHS).negate();

    @Autowired
    private TypeResolver resolver;

    @Bean
    public Docket publicApi() {
        return handleWebflux(
            new Docket(DocumentationType.SWAGGER_2)
                .groupName("api")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PUBLIC_API_PATHS)
                .build()
                .useDefaultResponseMessages(false)
        );
    }

    @Bean
    public Docket internalApi() {
        return handleWebflux(
            new Docket(DocumentationType.SWAGGER_2)
                .groupName("internal")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(INTERNAL_API_PATHS)
                .build()
                .useDefaultResponseMessages(false)
        );
    }

    @Bean
    public Docket technical() {
        return handleWebflux(
            new Docket(DocumentationType.SWAGGER_2)
                .groupName("technical")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(TECHNICAL_API_PATHS)
                .build()
                .useDefaultResponseMessages(false)
        );
    }

    private Docket handleWebflux(Docket docket){
        return docket
            .alternateTypeRules(
                new RecursiveAlternateTypeRule(
                    resolver,
                    asList(
                        newRule(
                            resolver.resolve(Mono.class, WildcardType.class),
                            resolver.resolve(WildcardType.class)
                        ),
                        newRule(
                            resolver.resolve(ResponseEntity.class, WildcardType.class),
                            resolver.resolve(WildcardType.class)
                        )
                    )
                )
            )
            .alternateTypeRules(
                new RecursiveAlternateTypeRule(
                    resolver,
                    asList(
                        newRule(
                            resolver.resolve(Flux.class, WildcardType.class),
                            resolver.resolve(List.class, WildcardType.class)
                        ),
                        newRule(
                            resolver.resolve(ResponseEntity.class, WildcardType.class),
                            resolver.resolve(WildcardType.class)
                        )
                    )
                )
            );
    }
}
