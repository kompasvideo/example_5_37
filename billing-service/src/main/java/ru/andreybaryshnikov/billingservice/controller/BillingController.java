package ru.andreybaryshnikov.billingservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.andreybaryshnikov.billingservice.model.Account;
import ru.andreybaryshnikov.billingservice.model.MinusMoney;
import ru.andreybaryshnikov.billingservice.model.PlusMoney;
import ru.andreybaryshnikov.billingservice.service.BillingService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing")
public class BillingController {
    private final BillingService billingService;

    @PostMapping("/add")
    public Account addMoney(@RequestHeader("X-Request-Id") String xRequestId,
                            @RequestHeader("X-UserId") String xUserId,
                            @RequestBody PlusMoney money){
        long id = Long.parseLong(xUserId);
        log.info("--- billing plus");
        log.info("--- billing X-Request-Id - " + xRequestId);
        log.info("--- billing X-UserId - " + xUserId);
        log.info("--- addMoney - ");
        log.info("--- addMoney - " + money);
        log.info("--- addMoney - " + money.getPlus());
        log.info("--- addMoney - " + money.getTotal());
        log.info("--- billing plus");
        return billingService.addMoney(xRequestId, id, money);
    }

    @PostMapping("/pay")
    public Account pay(@RequestHeader("X-Request-Id") String xRequestId,
                       @RequestHeader("X-UserId") String xUserId,
                       @RequestBody MinusMoney money) {
        log.info("--- billing pay");
        log.info("--- billing X-Request-Id - " + xRequestId);
        log.info("--- billing X-UserId - " + xUserId);
        log.info("--- payMoney - " + money);
        long id = Long.parseLong(xUserId);
        log.info("--- billing pay");
        return billingService.pay(xRequestId, id, money);
    }

    @GetMapping("/get")
    public double getBalance(@RequestHeader("X-Request-Id") String xRequestId,
                            @RequestHeader("X-UserId") String xUserId){
        log.info("--- getBalance");
        log.info("--- getBalance X-Request-Id - " + xRequestId);
        log.info("--- getBalance X-UserId - " + xUserId);
        long id = Long.parseLong(xUserId);
        log.info("--- getBalance id - " + id);
        return billingService.getBalance(xRequestId, id);
    }

    @GetMapping("/withdraw")
    public Account withdraw(@RequestHeader("X-Request-Id") String xRequestId,
                           @RequestHeader("X-UserId") String xUserId,
                           @RequestBody MinusMoney money){
        log.info("--- withdraw");
        log.info("--- withdraw X-Request-Id - " + xRequestId);
        log.info("--- withdraw X-UserId - " + xUserId);
        log.info("--- withdraw money - " + money);
        long id = Long.parseLong(xUserId);
        log.info("--- withdraw id - " + id);
        return billingService.withdraw(xRequestId, id, money);
    }
}
