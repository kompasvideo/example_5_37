package ru.andreybaryshnikov.deliveryservice.service;

public interface DeliveryService {
    boolean reserve(String xRequestId, long xUserId, String localDateTime);
}
