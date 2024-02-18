package ru.andreybaryshnikov.otus_auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PlusMoney {
    private double plus;
    private double total;
}
