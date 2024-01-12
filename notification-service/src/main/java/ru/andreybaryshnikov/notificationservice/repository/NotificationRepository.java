package ru.andreybaryshnikov.notificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreybaryshnikov.notificationservice.model.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(long userId);
}
