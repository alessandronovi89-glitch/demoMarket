package com.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quote {
    private String type;
    private String symbol;
    private BigDecimal bid;
    private BigDecimal  ask;
    private BigDecimal  last;
    private long volume24h; //se facciamo azioni e titoli allora è intero
    private long timestamp;
}
