package com.github.thriveframework.swagger;

import com.github.thriveframework.core.detection.ApiDetector;
import com.github.thriveframework.core.detection.model.dto.ThriveInstance;
import com.github.thriveframework.core.observing.ServiceObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@RestController
@Slf4j
public class SwaggerController {
    private SwaggerMerger swaggerMerger = new SwaggerMerger();

    private String mergedSwagger;

    @Autowired
    private ApiDetector apiDetector;

    @Autowired
    private ServiceObserver observer;

    @Value("${thrive.baseUrl:localhost:8080}")
    private String baseUrl;

    @PostConstruct
    public void init(){
        log.info("INIT");
        _init().log("INIT").subscribe();
        observer.watchChanges().log("changes").flatMap(x -> _init().log("OBSERVED")).subscribe();
    }

//    @EventListener(InstanceRegisteredEvent.class)
//    public void handler(InstanceRegisteredEvent e){
//        log.info("HANDLER "+e.hashCode()+" :: "+e);
//        _init().log("HANDLER "+e.hashCode()).subscribe();
//    }


    public Mono<Void> _init(){
        return apiDetector
            .getInstances()
            .filter(i -> i.hasApi() && i.hasSwagger())
            .map(ThriveInstance::getAddress)
            .log("address")
            .collectList()
            .log("addresses")
            .doOnNext(addresses ->
                mergedSwagger = swaggerMerger.getMergedDocs(baseUrl, addresses)
            )
            .then()
            .log("done");
    }

    //fixme throttle this or its an easy DDoS
    @GetMapping("/docs/v1/swagger/refresh")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> refreshSwagger(){
        return _init().log("ENDPOINT");
    }

    @GetMapping(value = "/docs/v1/swagger", produces = "application/json")
    public Mono<String> getMergedSwagger(){
        return Mono.justOrEmpty(mergedSwagger);
    }
}
