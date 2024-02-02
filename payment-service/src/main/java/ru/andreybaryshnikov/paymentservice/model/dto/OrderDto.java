package ru.andreybaryshnikov.paymentservice.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderDto {
    private UUID id;
    private String xRequestId;
    private long userId;
    private double price;
}