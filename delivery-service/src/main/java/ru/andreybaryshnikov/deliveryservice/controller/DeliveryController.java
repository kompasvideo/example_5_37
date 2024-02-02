package ru.andreybaryshnikov.deliveryservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.andreybaryshnikov.deliveryservice.service.DeliveryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/delivery")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping("/reserve")
    public boolean reserveCourier(@RequestHeader("X-Request-Id") String xRequestId,
                           @RequestHeader("X-UserId") long xUserId,
                           @RequestBody String localDateTime){
        return deliveryService.reserveCourier(xRequestId,xUserId, localDateTime);
    }

    @PostMapping("/cancel")
    public boolean cancelCourier(@RequestHeader("X-Request-Id") String xRequestId,
                           @RequestHeader("X-UserId") long xUserId){
        return deliveryService.cancelCourier(xRequestId,xUserId);
    }
}
