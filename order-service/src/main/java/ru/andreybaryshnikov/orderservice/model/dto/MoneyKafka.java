package ru.andreybaryshnikov.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class MoneyKafka {
    private LocalDateTime dateTime;
    private String operation;
    private double count;
    private double total;
    private String xRequestId;
    private long userId;
}