package com.github.filipmalczak.thrive.example.items;

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

import static java.util.Arrays.asList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

@Configuration
@EnableSwagger2WebFlux
public class SwaggerConfig {
    @Autowired
    private TypeResolver resolver;

    @Bean
    public Docket api() {
        return handleWebflux(
            new Docket(DocumentationType.SWAGGER_2)
                .groupName("api")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("/api[/]?.*"))
                .build()
                .useDefaultResponseMessages(false)
        );
    }

    @Bean
    public Docket internals() {
        return handleWebflux(
            new Docket(DocumentationType.SWAGGER_2)
                .groupName("internals")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("/api[/]?.*").negate())
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