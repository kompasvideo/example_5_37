package ru.andreybaryshnikov.paymentservice.service;

import ru.andreybaryshnikov.paymentservice.model.dto.OrderDto;

public interface PaymentService {
    boolean pay(String xRequestId, String xUserId, OrderDto orderDto);
}
