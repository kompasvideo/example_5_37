package ru.andreybaryshnikov.billingservice.service;

import ru.andreybaryshnikov.billingservice.model.Account;
import ru.andreybaryshnikov.billingservice.model.MinusMoney;
import ru.andreybaryshnikov.billingservice.model.PlusMoney;

import java.math.BigDecimal;

public interface BillingService {
    Account addMoney(String xRequestId, long userId, PlusMoney money);

    Account subMoney(String xRequestId, long userId, MinusMoney money);

    double getBalance(String xRequestId, long id);
}
