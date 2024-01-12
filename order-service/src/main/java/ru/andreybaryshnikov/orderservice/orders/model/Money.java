package ru.andreybaryshnikov.orderservice.orders.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class Money {
    private LocalDateTime dateTime;
    private String operation;
    private double count;
    private double total;
}
