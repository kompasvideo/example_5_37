package ru.andreybaryshnikov.catalogservice.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductDto {
    private UUID id;
    private String name;
    private double price;
    private String imageUrl;
    private double oldPrice;
    private String rating;
}
