package ru.andreybaryshnikov.paymentservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.andreybaryshnikov.paymentservice.model.dto.OrderDto;
import ru.andreybaryshnikov.paymentservice.service.PaymentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/pay")
    public boolean pay(@RequestHeader("X-Request-Id") String xRequestId,
                       @RequestHeader("X-UserId") String xUserId,
                       @RequestBody OrderDto orderDto) {
        log.info("--- 1 pay ---");
        boolean result  = paymentService.pay(xRequestId, xUserId, orderDto);
        log.info("--- 2 pay ---");
        return result;
    }
}
