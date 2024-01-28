package ru.andreybaryshnikov.cartservice.model.dto;

import lombok.Data;

@Data
public class CartDto {
    private String productId;
    private int quantity;
}
