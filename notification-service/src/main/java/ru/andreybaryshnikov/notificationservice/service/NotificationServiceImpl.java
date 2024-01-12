package ru.andreybaryshnikov.notificationservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.andreybaryshnikov.notificationservice.model.Money;
import ru.andreybaryshnikov.notificationservice.model.Notification;
import ru.andreybaryshnikov.notificationservice.repository.NotificationRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public void save(String xRequestId, String xUserId, Money money) {
        Notification notification = modelMapper.map(money, Notification.class);

        notification.setUserId(Long.parseLong(xUserId));
        notification.setXRequestId(xRequestId);
        notificationRepository.save(notification);
        log.info("------------");
        log.info("send to email");
        log.info("xUserId - " + xUserId);
        log.info(money.toString());
        log.info("xRequestId - " + xRequestId);
        log.info("------------");
    }

    @Transactional
    @Override
    public List<Notification> get(String xRequestId, String xUserId) {
        List<Notification> notifications = notificationRepository.findByUserId(Long.parseLong(xUserId));
        log.info("------------");
        log.info("get messages to notification");
        log.info("xUserId - " + xUserId);
        log.info("xRequestId - " + xRequestId);
        log.info(notifications.toString());
        log.info("------------");
        return notifications;
    }
}
