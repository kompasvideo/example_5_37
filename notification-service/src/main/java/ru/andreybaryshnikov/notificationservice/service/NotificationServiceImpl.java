package ru.andreybaryshnikov.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import ru.andreybaryshnikov.notificationservice.model.Money;
import ru.andreybaryshnikov.notificationservice.model.MoneyKafka;
import ru.andreybaryshnikov.notificationservice.model.Notification;
import ru.andreybaryshnikov.notificationservice.repository.NotificationRepository;

import java.io.IOException;
import java.io.StringReader;
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

    @KafkaListener(topics = "NotificafionTopic", containerFactory = "kafkaListenerContainerFactoryString")
    public void send(String message) {
        log.info("Receive message {}", message);
        StringReader reader = new StringReader(message);
        log.info("-- send 1 --");
        ObjectMapper mapper = new ObjectMapper();
        log.info("-- send 2 --");
        mapper.findAndRegisterModules();
        log.info("-- send 3 --");
        MoneyKafka moneyKafka;
        try {
            moneyKafka = mapper.readValue(reader, MoneyKafka.class);
        } catch (IOException e) {
            log.info("-- send 4 -- IOException - " + e);
            return;
        }
        log.info("-- send 5 --");
        Notification notification = modelMapper.map(moneyKafka, Notification.class);
        log.info("-- send 6 --");
        notification.setUserId(moneyKafka.getUserId());
        log.info("-- send 7 --");
        notification.setXRequestId(moneyKafka.getXRequestId());
        log.info("-- send 8 --");
        notificationRepository.save(notification);
        log.info("-- send 9 --");
        log.info("-- send 10 -- xUserId - " + moneyKafka.getUserId());
        log.info(moneyKafka.toString());
        log.info("-- send 11 -- xRequestId - " + moneyKafka.getXRequestId());
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
