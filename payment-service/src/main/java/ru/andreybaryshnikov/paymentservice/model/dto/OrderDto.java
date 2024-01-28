package ru.andreybaryshnikov.paymentservice.model.dto;

import lombok.Data;
import ru.andreybaryshnikov.paymentservice.model.PaymentMethod;

import java.util.UUID;

@Data
public class OrderDto {
    private UUID id;
    private String xRequestId;
    private long userId;
    private double price;
    private PaymentMethod paymentMethod; // метод оплаты
}