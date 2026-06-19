package com.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    private String accountId;
    private String symbol;
    private String side;
    private int size;
    private BigDecimal avgEntryPrice;
    private BigDecimal markPrice;
    private BigDecimal unrealizedPnL; //guadagno o perdita
}
