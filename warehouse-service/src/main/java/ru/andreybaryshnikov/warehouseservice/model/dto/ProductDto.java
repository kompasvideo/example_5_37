package ru.andreybaryshnikov.warehouseservice.model.dto;

import lombok.Data;

@Data
public class ProductDto {
    private String productId;
    private int count;
}
