package ru.andreybaryshnikov.billingservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MinusMoney {
    private double minus;
    private double total;
}
