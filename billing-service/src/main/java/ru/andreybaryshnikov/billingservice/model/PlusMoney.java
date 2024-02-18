package ru.andreybaryshnikov.billingservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PlusMoney {
    private double plus;
    private double total;
}
