package com.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quote {
    private String type;
    private String symbol;
    private double bid;
    private double ask;
    private double last;
    private double volume24h;
    private long timestamp;
}
