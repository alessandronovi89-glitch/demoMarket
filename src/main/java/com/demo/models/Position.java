package com.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    private String accountId;
    private String symbol;
    private String side;
    private int size;
    private double avgEntryPrice;
    private double markPrice;
    private double unrealizedPnL;
}
