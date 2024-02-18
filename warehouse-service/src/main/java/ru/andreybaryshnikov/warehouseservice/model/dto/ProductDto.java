package ru.andreybaryshnikov.warehouseservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProductDto {
    private String productId;
    private int count;
}
