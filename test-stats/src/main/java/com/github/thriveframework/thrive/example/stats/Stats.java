package com.github.thriveframework.thrive.example.stats;

import lombok.Data;

@Data
public class Stats {
    private long total = 0;
    private long count = 0;

    public void addItem(long size){
        total += size;
        count++;
    }
}
