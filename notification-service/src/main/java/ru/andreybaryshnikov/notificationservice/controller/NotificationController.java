package ru.andreybaryshnikov.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.andreybaryshnikov.notificationservice.model.Money;
import ru.andreybaryshnikov.notificationservice.model.Notification;
import ru.andreybaryshnikov.notificationservice.service.NotificationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/send")
    public void send(@RequestHeader("X-Request-Id") String xRequestId,
                     @RequestHeader("X-UserId") String xUserId,
                     @RequestBody Money money){
        log.info("--- notification send");
        log.info("--- notification X-Request-Id - " + xRequestId);
        log.info("--- notification X-UserId - " + xUserId);
        log.info("--- send - ");
        log.info("--- send - " + money);
        log.info("--- send - " + money.getDateTime());
        log.info("--- send - " + money.getTotal());
        log.info("--- notification send");
        notificationService.save(xRequestId, xUserId, money);
    }

    @PostMapping("/get")
    public List<Notification> get(@RequestHeader("X-Request-Id") String xRequestId,
                                  @RequestHeader("X-UserId") String xUserId){
        List<Notification> notifications = notificationService.get(xRequestId, xUserId);
        log.info("--- notification get");
        log.info("--- notification X-Request-Id - " + xRequestId);
        log.info("--- notification X-UserId - " + xUserId);
        log.info("--- get - ");
        log.info("--- notification get");
        return notifications;
    }
}
