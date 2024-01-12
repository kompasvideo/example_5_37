package ru.andreybaryshnikov.notificationservice.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Money {
    private LocalDateTime dateTime;
    private String operation;
    private BigDecimal count;
    private BigDecimal total;
}
