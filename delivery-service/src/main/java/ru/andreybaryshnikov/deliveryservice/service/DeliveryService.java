package ru.andreybaryshnikov.deliveryservice.service;

public interface DeliveryService {
    boolean reserveCourier(String xRequestId, long xUserId, String localDateTime);

    boolean cancelCourier(String xRequestId, long xUserId);
}
