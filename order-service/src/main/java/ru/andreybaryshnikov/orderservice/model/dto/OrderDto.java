package ru.andreybaryshnikov.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OrderDto {
    private String productId;
    private int count;
    private double price;
}
