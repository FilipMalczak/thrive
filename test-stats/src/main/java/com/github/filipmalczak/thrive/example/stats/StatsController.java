package com.github.filipmalczak.thrive.example.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class StatsController {
    private Stats stats = new Stats();


    @GetMapping("/api/v1/items/size/statistics")
    public Stats getStats(){
        log.info("Returning "+stats);
        return stats;
    }

    //fixme should be internal
    @PostMapping("/api/v1/items/size")
    public void updateStatsWebhook(@RequestBody long size){
        log.info("Pre update "+stats);
        stats.addItem(size);
        log.info("Post update "+stats);
    }

}
