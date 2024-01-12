package ru.andreybaryshnikov.billingservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.andreybaryshnikov.billingservice.model.Account;
import ru.andreybaryshnikov.billingservice.model.MinusMoney;
import ru.andreybaryshnikov.billingservice.model.PlusMoney;
import ru.andreybaryshnikov.billingservice.service.BillingService;

import java.math.BigDecimal;

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

    @PostMapping("/sub")
    public Account subMoney(@RequestHeader("X-Request-Id") String xRequestId,
                            @RequestHeader("X-UserId") String xUserId,
                            @RequestBody MinusMoney money) {
        long id = Long.parseLong(xUserId);
        log.info("--- billing minus");
        log.info("--- billing X-Request-Id - " + xRequestId);
        log.info("--- billing X-UserId - " + xUserId);
        log.info("--- subMoney - " + money);
        log.info("--- billing minus");
        return billingService.subMoney(xRequestId, id, money);
    }

    @GetMapping("/get")
    public double getBalance(@RequestHeader("X-Request-Id") String xRequestId,
                            @RequestHeader("X-UserId") String xUserId){
        long id = Long.parseLong(xUserId);
        log.info("--- getBalance");
        log.info("--- getBalance X-Request-Id - " + xRequestId);
        log.info("--- getBalance X-UserId - " + xUserId);
        log.info("--- getBalance id - " + id);
        log.info("--- getBalance");
        return billingService.getBalance(xRequestId, id);
    }
}
