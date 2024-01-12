package ru.andreybaryshnikov.notificationservice.service;

import ru.andreybaryshnikov.notificationservice.model.Money;
import ru.andreybaryshnikov.notificationservice.model.Notification;

import java.util.List;

public interface NotificationService {
    void save(String xRequestId, String xUserId, Money money);

    List<Notification> get(String xRequestId, String xUserId);
}
