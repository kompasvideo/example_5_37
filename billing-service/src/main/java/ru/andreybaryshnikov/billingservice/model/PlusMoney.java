package ru.andreybaryshnikov.billingservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class PlusMoney {
    private double plus;
    private double total;
}
