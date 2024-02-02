package ru.andreybaryshnikov.orderservice.service;

import ru.andreybaryshnikov.orderservice.model.dto.DeliveryLocationDto;
import ru.andreybaryshnikov.orderservice.model.dto.OrderDto;
import ru.andreybaryshnikov.orderservice.model.Order;
import ru.andreybaryshnikov.orderservice.model.PaymentMethod;

import java.util.UUID;

public interface OrderService {
    Order create(OrderDto orderDto, String xRequestId, String xUserId);

    Order getToId(UUID uuid);

    Order chooseADeliveryLocation(String xRequestId, String xUserId, DeliveryLocationDto deliveryLocationDto);

    Order chooseAPaymentMethod(String xRequestId, String xUserId, PaymentMethod paymentMethod);

    Order chooseTheDeliveryTime(String xRequestId, String xUserId, String dateTime);

    boolean payAndSendMessage(String xRequestId, String xUserId);

    Order courierReserve(String xRequestId, String xUserId);

    boolean clear(UUID uuid);
}
